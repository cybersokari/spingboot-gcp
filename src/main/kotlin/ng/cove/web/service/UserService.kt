package ng.cove.web.service

import com.google.firebase.auth.FirebaseAuth
import ng.cove.web.component.SmsOtpService
import ng.cove.web.data.model.Member
import ng.cove.web.data.model.SecurityGuard
import ng.cove.web.data.model.UserType
import ng.cove.web.data.repo.MemberRepo
import ng.cove.web.data.repo.SecurityGuardRepo
import ng.cove.web.http.body.LoginBody
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
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


    fun getOtpForLogin(phone: String, userType: UserType): ResponseEntity<*> {
        if (userType == UserType.Member) {
            memberRepo.findByPhoneAndCommunityIsNotNull(phone)
                ?: return ResponseEntity.badRequest().body("$phone is not a member of a community")
        } else {
            guardRepo.findByPhoneAndCommunityIdIsNotNull(phone)
                ?: return ResponseEntity.badRequest().body("$phone is not guard of a community")
        }

        val otpResult = smsOtp.sendOtp(phone)
        return if (otpResult != null) {
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
            val phone = smsOtp.verifyOtp(login.otp, login.ref)
                ?: return ResponseEntity.badRequest().body("Invalid code")

            val userId: String
            var userTypeForClaims = userType

            if (userType == UserType.Member) {
                val member = memberRepo.findByPhone(phone)!!
                //TODO: Redesign this if possible: Its only going to run on first user login, but check forever
                if (member.phoneVerifiedAt == null) {
                    member.phoneVerifiedAt = Date()
                }

                member.deviceId = login.deviceId
                member.deviceName = login.deviceName
                member.lastLoginAt = Date()
                memberRepo.save(member)

                userId = member.id!!
                // Switch admin user type for JWT claims
                if (member.community!!.adminIds!!.contains(userId)) {
                    userTypeForClaims = UserType.Admin
                }

            } else {
                val guard = guardRepo.findByPhone(phone)!!
                //TODO: Redesign this if possible: Its only going to run on first user login, but check forever
                if (guard.phoneVerifiedAt == null) {
                    guard.phoneVerifiedAt = Date()
                }

                userId = guard.id!!

                // Update device info
                guard.deviceId = login.deviceId
                guard.deviceName = login.deviceName
                guard.lastLoginAt = Date()
                guardRepo.save(guard)
            }


            val firebaseAuth = FirebaseAuth.getInstance()

            //Revoke refresh token for old devices if any
            try {
                firebaseAuth.revokeRefreshTokens(userId)
            } catch (_: Exception) {
            }

            // Set Admin claim for JWT
            val claims = mapOf("type" to userTypeForClaims.name)
            val customToken = firebaseAuth.createCustomToken(userId, claims)

            return ResponseEntity.ok().body(customToken)
        } catch (e: Exception) {
            logger.error(e.localizedMessage)
            return ResponseEntity.internalServerError().body(e.localizedMessage)
        }
    }

    @Cacheable(value = ["member"])
    fun getMemberById(id: String): Member? {
        return memberRepo.findById(id).orElse(null)
    }

    @Cacheable(value = ["guard"])
    fun getGuardById(id: String): SecurityGuard? {
        return guardRepo.findById(id).orElse(null)
    }

}
