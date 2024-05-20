package ng.cove.web.service

import com.google.firebase.auth.FirebaseAuth
import ng.cove.web.component.SmsOtpService
import ng.cove.web.data.model.PhoneOtp
import ng.cove.web.data.model.UserType
import ng.cove.web.data.repo.MemberPhoneOtpRepo
import ng.cove.web.data.repo.MemberRepo
import ng.cove.web.data.repo.SecurityGuardRepo
import ng.cove.web.http.body.LoginBody
import ng.cove.web.http.body.OtpRefBody
import ng.cove.web.util.CacheNames
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

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var memberRepo: MemberRepo

    @Autowired
    lateinit var guardRepo: SecurityGuardRepo

    @Autowired
    lateinit var smsOtp: SmsOtpService

    @Autowired
    lateinit var cacheManager: CaffeineCacheManager

    @Autowired
    lateinit var otpRepo: MemberPhoneOtpRepo

    @Value("\${otp.trial-limit}")
    var maxDailyOtpTrial: Int = 0


    fun getOtpForLogin(phone: String, userType: UserType): ResponseEntity<*> {
        if (userType == UserType.Member) {
            memberRepo.findByPhoneAndCommunityIsNotNull(phone)
                ?: return ResponseEntity.badRequest().body("$phone is not a member of a community")
        } else {
            guardRepo.findByPhoneAndCommunityIdIsNotNull(phone)
                ?: return ResponseEntity.badRequest().body("$phone is not guard of a community")
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
    fun verifyPhoneOtp(login: LoginBody, userType: UserType): ResponseEntity<*> {

        try {
            // If user is tester return the phone number, otherwise verify OTP
            val phone = verifyTesterOtp(login, userType) ?: smsOtp.verifyOtp(login.otp, login.ref)
            ?: return ResponseEntity.badRequest().body("Invalid code")

            val userId: String

            if (userType == UserType.Member) {
                val member = memberRepo.findByPhone(phone)!!
                //TODO: Redesign this if possible: Its only going to run on first user login, but check forever
                if (member.phoneVerifiedAt == null) {
                    member.phoneVerifiedAt = Date()
                }

                member.deviceName = login.deviceName
                member.lastLoginAt = Date()
                memberRepo.save(member)

                userId = member.id!!
                // Update cache
                cacheManager.getCache(CacheNames.MEMBERS)?.put(userId, member)

            } else {
                val guard = guardRepo.findByPhone(phone)!!
                //TODO: Redesign this if possible: Its only going to run on first user login, but check forever
                if (guard.phoneVerifiedAt == null) {
                    guard.phoneVerifiedAt = Date()
                }

                userId = guard.id!!

                // Update device info
                guard.deviceName = login.deviceName
                guard.lastLoginAt = Date()
                guardRepo.save(guard)
                // Update cache
                cacheManager.getCache(CacheNames.GUARDS)?.put(userId, guard)
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
            val claims = mapOf("type" to userType.name)

            val customToken = firebaseAuth.createCustomToken(userId, claims)

            return ResponseEntity.ok().body(customToken)
        } catch (e: Exception) {
            logger.error(e.localizedMessage)
            return ResponseEntity.internalServerError().body(e.localizedMessage)
        }
    }

    fun getTesterId(phone: String, userType: UserType): String? {
        return if (userType == UserType.Member) {
            memberRepo.findFirstByTestOtpIsNotNullAndPhone(phone)?.id
        } else {
            guardRepo.findFirstByTestOtpIsNotNullAndPhone(phone)?.id
        }
    }

    // Return null if user is not a tester
    fun verifyTesterOtp(login: LoginBody, userType: UserType): String? {
        return if (userType == UserType.Member) {
            memberRepo.findByIdAndTestOtp(login.ref, login.otp)?.phone
        } else {
            guardRepo.findByIdAndTestOtp(login.ref, login.otp)?.phone
        }
    }

}
