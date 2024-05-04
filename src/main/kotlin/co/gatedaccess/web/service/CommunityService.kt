package co.gatedaccess.web.service

import co.gatedaccess.web.data.model.*
import co.gatedaccess.web.data.repo.*
import co.gatedaccess.web.http.body.GuardInputBody
import co.gatedaccess.web.util.ApiResponseMessage
import co.gatedaccess.web.util.CodeGenerator
import com.google.firebase.auth.FirebaseAuth
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.NoSuchElementException

@Component
class CommunityService {

    @Autowired
    private lateinit var notificationService: NotificationService

    @Autowired
    var environment: Environment? = null
    @Autowired
    lateinit var communityRepo: CommunityRepo
    @Autowired
    lateinit var memberRepo: MemberRepo
    @Autowired
    lateinit var joinRequestRepo: JoinRequestRepo

    @Autowired
    lateinit var securityGuardRepo: SecurityGuardRepo

    @Autowired
    private val codeGenerator: CodeGenerator? = null

    private val logger = LoggerFactory.getLogger(this::class.simpleName)


//    @Transactional
//    fun getSecurityGuardOtpForAdmin(adminUserId: String?): ResponseEntity<*> {
//        val community = communityRepo!!.findCommunityBySuperAdminId(adminUserId)
//            ?: return ResponseEntity.badRequest().body("User not a community admin")
//
//        // Delete old before creating a new one
//        securityGuardOtpRepo!!.deleteByCommunityId(community.id)
//
//
//        // Retry otp generation if any duplicate occur
//        var expiryTime: Date? = null
//        var guardOtp = SecurityGuardOtp()
//        val codeExpiryDurationInSecs =
//            environment!!.getProperty("security-guard.otp.duration-in-secs")!!.toInt()
//        while (guardOtp.id == null) { // Check for MongoDb ID to know if save is successful
//
//            guardOtp.setCode(codeGenerator!!.getCode(CodeType.Guard))
//
//            var futureDateTime = Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
//            futureDateTime = futureDateTime.plusSeconds(codeExpiryDurationInSecs.toLong())
//            expiryTime = Date.from(Instant.from(futureDateTime))
//            guardOtp.expireAt = expiryTime
//
//            try {
//                guardOtp = securityGuardOtpRepo!!.save(guardOtp)
//            } catch (ignored: DuplicateKeyException) {
//            }
//        }
//        return ResponseEntity.ok(GuardOtpBody(guardOtp.getCode(), expiryTime))
//    }

    /**
     * @param adminUserId Identity of the admin of the community
     * @param requestId
     * @param accept      if the request was accepted or rejected
     * @return status of the request
     */
    @Transactional
    fun handleCommunityJoinRequest(adminUserId: String, requestId: JoinRequestId, accept: Boolean): ResponseEntity<*> {

        val community = communityRepo.findCommunityByIdAndSuperAdminId(requestId.communityId, adminUserId)
            ?: return ResponseEntity.badRequest()
                .body(ApiResponseMessage.USER_NOT_SUPER_ADMIN)

        try {
            val joinRequest = joinRequestRepo.findById(requestId).orElseThrow()!!

            joinRequest.acceptedAt?.let {
                return ResponseEntity.accepted()
                    .body(ApiResponseMessage.REQUEST_ALREADY_ACCEPTED)
            }

            // Admin rejects request
            if(!accept){
                joinRequestRepo.deleteById(requestId)
                //TODO:Notify referrer of rejection
                return ResponseEntity.ok().body("Request rejected")
            }

            // Update or create a Member
            val memberPhone = joinRequest.id!!.phone
            val member = memberRepo.findByPhone(memberPhone)?: Member()

            member.firstName = joinRequest.firstName
            member.lastName = joinRequest.lastName
            member.community = community
            member.phone = memberPhone
            memberRepo.save(member)

            joinRequest.acceptedAt = Date()
            joinRequestRepo.save(joinRequest)
            //Delete all pending request with this phone number
            joinRequestRepo.deleteAllByIdPhoneAndAcceptedAtIsNull(requestId.phone)

            //TODO:Notify referrer of acceptance
            return ResponseEntity.status(HttpStatus.OK).body("Request accepted")
        }catch (_: NoSuchElementException){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseMessage.REQUEST_CANT_BE_FOUND)
        }
    }


    @Transactional
    fun inviteUserToCommunity(request: JoinRequest, referrer: Member): ResponseEntity<*> {
        try {

            val community = referrer.community
                ?: return ResponseEntity.badRequest()
                    .body("Referrer is not part of any community at this time")

            val requestId = request.id!!
            if(community.id != requestId.communityId){
                return ResponseEntity.badRequest()
                    .body("Referrer can only invite a user to their community")
            }

            if (community.superAdminId == null) {
                return ResponseEntity.badRequest().body("Community does not have an super admin")
            }

            if(memberRepo.existsByPhoneAndCommunityIsNotNull(requestId.phone)){
                return ResponseEntity.badRequest().body("User is already part of a community")
            }

            if (joinRequestRepo.existsByIdAndAcceptedAtIsNull(requestId)){
                return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("User already has a pending request")
            }

            request.referrerId = referrer.id
            joinRequestRepo.save(request)

            //TODO: Notify community admin
            return ResponseEntity.ok()
                .body("Request to join community sent")

        }catch (_: NoSuchElementException){
            return ResponseEntity.badRequest().body("Referrer not found")
        }
    }

    fun addSecurityGuardToCommunity(guardData: GuardInputBody, userId: String): ResponseEntity<*>{

        val community = communityRepo.findByAdminIdsContains(userId)
            ?: return ResponseEntity.badRequest().body("User not an Admin in a community")

        val phone = guardData.phone
        if(securityGuardRepo.existsByPhone(phone)){
            ResponseEntity.badRequest().body("$phone is already a Guard in a community")
        }

        val guard = SecurityGuard()
        guard.phone = guardData.phone
        guard.firstName = guardData.firstName
        guard.lastName = guardData.lastName
        guard.communityId = community.id
        securityGuardRepo.save(guard)

        return ResponseEntity.ok().body(guardData)
    }

    fun removeSecurityGuardFromCommunity(guardId: String, userId: String): ResponseEntity<*>{

        val community = communityRepo.findByAdminIdsContains(userId)
            ?: return ResponseEntity.badRequest().body("User not an Admin in a community")

        try {
            val guardCommunityId = securityGuardRepo.findById(guardId).orElseThrow()!!.communityId

            if(guardCommunityId == community.id){

                securityGuardRepo.deleteById(guardId)
                FirebaseAuth.getInstance().revokeRefreshTokens(guardId)
                return ResponseEntity.ok().body("Guard has been removed from community")
            }else{
                return ResponseEntity.badRequest().body("User not an Admin in Guard's community")
            }

        }catch (e: NoSuchElementException){
            logger.warn( e.localizedMessage)
            return ResponseEntity.badRequest().body(e.localizedMessage)
        }


    }
}
