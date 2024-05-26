package ng.cove.web.service

import com.google.firebase.auth.FirebaseAuth
import ng.cove.web.data.model.*
import ng.cove.web.data.repo.*
import ng.cove.web.http.body.LoginBody
import ng.cove.web.http.body.OtpRefBody
import ng.cove.web.util.CacheName
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.*


@Service
class UserService {

    @Autowired
    private lateinit var communityRepo: CommunityRepo
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var memberRepo: MemberRepo

    @Autowired
    lateinit var guardRepo: SecurityGuardRepo

    @Autowired
    lateinit var adminRepo: AdminRepo

    @Autowired
    lateinit var smsOtp: SmsOtpService

    @Autowired
    lateinit var cacheManager: CaffeineCacheManager

    @Autowired
    lateinit var otpRepo: MemberPhoneOtpRepo

    @Value("\${otp-trial-limit}")
    var maxDailyOtpTrial: Int = 1


    fun getOtpForLogin(phone: String, userType: UserType): ResponseEntity<*> {

        when (userType) {
            UserType.Member -> {
                memberRepo.findByPhoneAndCommunityIdIsNotNull(phone)
                    ?: return ResponseEntity.badRequest().body("$phone is not a member of a community")
            }

            UserType.Guard -> {
                guardRepo.findByPhoneAndCommunityIdIsNotNull(phone)
                    ?: return ResponseEntity.badRequest().body("$phone is not guard of a community")
            }

            UserType.Admin -> {
                val admin = adminRepo.findByPhoneAndCommunityIdIsNotNull(phone)
                if (admin == null || !communityRepo.existsByAdminsContains(admin.id!!)) {
                    return ResponseEntity.badRequest().body("$phone is not an admin of a community")
                }
            }
        }

        // Check if user is a tester
        getTesterId(phone, userType)?.let {
            val future = Date.from(Instant.now().plus(Duration.ofDays(1)))
            return ResponseEntity.ok().body(OtpRefBody(it, phone, future, 100))
        }

        val aDayAgo = Date.from(Instant.now().minus(Duration.ofDays(1)))

        var trialCount = otpRepo.countByPhoneAndCreatedAtIsAfter(phone, aDayAgo).toInt()
        if (trialCount >= maxDailyOtpTrial) {
            return ResponseEntity.badRequest().body("Trial exceeded try again later")
        }

        val otpResult = smsOtp.sendOtp(phone)
        return if (otpResult != null) {
            trialCount++
            otpResult.dailyTrialLeft = maxDailyOtpTrial - trialCount
            val phoneOtp = PhoneOtp()
            phoneOtp.phone = phone
            phoneOtp.ref = otpResult.ref
            phoneOtp.type = userType
            phoneOtp.expireAt = otpResult.expireAt
            otpRepo.save(phoneOtp)
            ResponseEntity.ok().body(otpResult)
        } else {
            ResponseEntity.internalServerError().body("OTP provider error")
        }

    }

    /**
     * Verify phone OTP for all user types
     */
    fun verifyPhoneOtp(login: LoginBody): ResponseEntity<*> {

        try {
            // If user is tester return the phone number, otherwise verify OTP
            val phone = verifyTesterOtp(login) ?: smsOtp.verifyOtp(login.otp, login.ref)
            ?: return ResponseEntity.badRequest().body("Invalid code")

            val userId: String

            when (login.type) {
                UserType.Member -> {
                    var member = memberRepo.findByPhone(phone)!!
                    userId = member.id!!
                    member = setUpValidUserForLogin(member, login.deviceName, CacheName.MEMBERS) as Member
                    memberRepo.save(member)
                }

                UserType.Guard -> {
                    var guard = guardRepo.findByPhone(phone)!!
                    userId = guard.id!!
                    guard = setUpValidUserForLogin(guard, login.deviceName, CacheName.GUARDS) as SecurityGuard
                    guardRepo.save(guard)
                }

                UserType.Admin -> {
                    var admin = adminRepo.findByPhone(phone)!!
                    userId = admin.id!!
                    admin = setUpValidUserForLogin(admin, login.deviceName, CacheName.ADMINS) as Admin
                    adminRepo.save(admin)
                }
            }

            val firebaseAuth = FirebaseAuth.getInstance()

            try {
                //Revoke refresh token for old devices if any
                firebaseAuth.revokeRefreshTokens(userId)
                // Clear OTP limit for this user
                otpRepo.deleteAllByPhone(phone)
            } catch (_: Exception) {
            }

            // Set Admin claim for JWT
            val claims = mapOf("type" to login.type.name)

            val customToken = firebaseAuth.createCustomToken(userId, claims)

            return ResponseEntity.ok(customToken)
        } catch (e: Exception) {
            logger.error(e.localizedMessage)
            return ResponseEntity.internalServerError().body(e.localizedMessage)
        }
    }

    private fun setUpValidUserForLogin(user: User, deviceName: String, cacheName: String): Any {
        if (user.phoneVerifiedAt == null) {
            user.phoneVerifiedAt = Date()
        }

        // Update device info
        user.deviceName = deviceName
        user.lastLoginAt = Date()
        cacheManager.getCache(cacheName)?.evict(user.id!!)
        return user
    }

    private fun getTesterId(phone: String, userType: UserType): String? {
        return when (userType) {
            UserType.Member -> {
                memberRepo.findFirstByTestOtpIsNotNullAndPhone(phone)?.id
            }

            UserType.Guard -> {
                guardRepo.findFirstByTestOtpIsNotNullAndPhone(phone)?.id
            }

            UserType.Admin -> {
                adminRepo.findFirstByTestOtpIsNotNullAndPhone(phone)?.id
            }
        }
    }

    // Return null if user is not a tester
    private fun verifyTesterOtp(login: LoginBody): String? {
        return when (login.type) {
            UserType.Member -> {
                memberRepo.findByIdAndTestOtp(login.ref, login.otp)?.phone
            }

            UserType.Guard -> {
                guardRepo.findByIdAndTestOtp(login.ref, login.otp)?.phone
            }

            UserType.Admin -> {
                adminRepo.findByIdAndTestOtp(login.ref, login.otp)?.phone
            }
        }
    }

}
