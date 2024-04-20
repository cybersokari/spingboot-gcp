package co.gatedaccess.web.service

import co.gatedaccess.web.data.model.JoinCommunityRequest
import co.gatedaccess.web.data.model.SecurityGuardDevice
import co.gatedaccess.web.data.model.SecurityGuardOtp
import co.gatedaccess.web.data.repo.*
import co.gatedaccess.web.http.response.GuardOtpBody
import co.gatedaccess.web.http.response.TokenBody
import co.gatedaccess.web.util.ApiResponseMessage
import co.gatedaccess.web.util.CodeGenerator
import co.gatedaccess.web.util.CodeType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.mongodb.DuplicateKeyException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.util.Map

@Component
open class CommunityService {
    @Autowired
    var communityRepo: CommunityRepo? = null

    @Autowired
    var memberRepo: MemberRepo? = null

    @Autowired
    var joinCommunityRequestRepo: JoinCommunityRequestRepo? = null

    @Autowired
    var securityGuardOtpRepo: SecurityGuardOtpRepo? = null

    @Autowired
    var securityGuardDeviceRepo: SecurityGuardDeviceRepo? = null

    @Autowired
    var environment: Environment? = null

    @Autowired
    private val codeGenerator: CodeGenerator? = null

    @Transactional
    open fun getCustomTokenForSecurityGuard(otp: String?, deviceName: String?): ResponseEntity<*> {
        val securityGuardOtp = securityGuardOtpRepo!!.findByCode(otp)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid Otp")

        val otpCreatedDate = securityGuardOtp.createdAt!!.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        val duration = Duration.between(otpCreatedDate, LocalDateTime.now())

        val allowedDurationInSecs =
            environment!!.getRequiredProperty("security-guard.otp.duration-in-secs", Int::class.java)
        if (duration.toSeconds() > allowedDurationInSecs) {
            securityGuardOtpRepo!!.delete(securityGuardOtp)
            return ResponseEntity.status(HttpStatus.GONE).body("Otp Expired")
        }

        var device = SecurityGuardDevice.Builder()
            .withDeviceName(deviceName)
            .withCommunityId(securityGuardOtp.communityId).build()
        device = securityGuardDeviceRepo!!.save(device)
        try {
            val customToken = FirebaseAuth.getInstance()
                .createCustomToken(device.id, Map.of<String, Any>("user", "guard"))
            return ResponseEntity.ok().body(TokenBody(customToken))
        } catch (e: FirebaseAuthException) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.localizedMessage)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.localizedMessage)
        }
    }

    @Transactional
    open fun getSecurityGuardOtpForAdmin(adminUserId: String?): ResponseEntity<*> {
        val community = communityRepo!!.findCommunityBySuperAdminId(adminUserId)
            ?: return ResponseEntity.badRequest().body("User not a community admin")

        // Delete old before creating a new one
        securityGuardOtpRepo!!.deleteByCommunityId(community.id)


        // Retry otp generation if any duplicate occur
        var expiryTime: Date? = null
        var guardOtp = SecurityGuardOtp()
        val codeExpiryDurationInSecs =
            environment!!.getProperty("security-guard.otp.duration-in-secs")!!.toInt()
        while (guardOtp.id == null) { // Check for MongoDb ID to know if save is successful

            guardOtp.setCode(codeGenerator!!.getCode(CodeType.guard))

            var futureDateTime = Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
            futureDateTime = futureDateTime.plusSeconds(codeExpiryDurationInSecs.toLong())
            expiryTime = Date.from(Instant.from(futureDateTime))
            guardOtp.expireAt = expiryTime

            try {
                guardOtp = securityGuardOtpRepo!!.save(guardOtp)
            } catch (ignored: DuplicateKeyException) {
            }
        }
        return ResponseEntity.ok(GuardOtpBody(guardOtp.getCode(), expiryTime))
    }

    /**
     * @param adminUserId Identity of the admin of the community
     * @param requestId
     * @param accept      if the request was accepted or rejected
     * @return status of the request
     */
    @Transactional
    open fun handleCommunityJoinRequest(adminUserId: String?, requestId: String?, accept: Boolean): ResponseEntity<String?> {
        val community = communityRepo!!.findCommunityBySuperAdminId(adminUserId)
            ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseMessage.USER_NOT_SUPER_ADMIN)
        val joinRequest = joinCommunityRequestRepo!!.findJoinCommunityRequestById(requestId)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseMessage.REQUEST_CANT_BE_FOUND)
        val memberId = joinRequest.member!!.id
        if (joinCommunityRequestRepo!!.existsJoinCommunityRequestByMemberIdAndAcceptedAtIsNotNull(memberId)) {
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(ApiResponseMessage.REQUEST_ALREADY_ACCEPTED)
        }
        if (accept) {
            val member = joinRequest.member
            member!!.community = community
            memberRepo!!.save(member)
            joinRequest.acceptedAt = Date()
        } else {
            joinRequest.rejectedAt = Date()
        }
        joinCommunityRequestRepo!!.save(joinRequest)

        return ResponseEntity.status(HttpStatus.OK).body("Request updated")
    }


    @Transactional
    open fun joinWithInviteCode(inviteCode: String?, userId: String?): ResponseEntity<String?> {
        val referrer = memberRepo!!.findMemberByInviteCode(inviteCode)
            ?: return ResponseEntity.badRequest().body("Invite code is not valid")

        val community = referrer.community
            ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Referrer is not part of any community at this time")

        if (community.superAdmin == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Community does not have an Admin")
        }

        val member = memberRepo!!.findMemberById(userId)

        if (member.community != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is already part of a community")
        }
        if (member.photoUrl == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponseMessage.PHOTO_IS_REQUIRED)
        }

        if (joinCommunityRequestRepo!!.findJoinCommunityRequestByMemberIdAndAcceptedAtIsNull(userId) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("User already have a pending request that has not been accepted")
        }

        val request = JoinCommunityRequest.Builder()
            .withMember(member)
            .withReferrer(referrer)
            .withCommunity(community).build()
        joinCommunityRequestRepo!!.save(request)

        /*Remove used invite code from referrer's document*/
        referrer.inviteCode = null
        memberRepo!!.save(referrer)

        //TODO: Notify community admin
        return ResponseEntity.status(HttpStatus.OK)
            .body("Request to join community sent")
    }
}
