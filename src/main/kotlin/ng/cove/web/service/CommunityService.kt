package ng.cove.web.service

import com.google.firebase.auth.FirebaseAuth
import com.mongodb.DuplicateKeyException
import ng.cove.web.data.model.*
import ng.cove.web.data.repo.*
import ng.cove.web.http.body.GuardInfoBody
import ng.cove.web.util.AccessCodeGenerator
import ng.cove.web.util.ApiResponseMessage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class CommunityService {

    @Autowired
    private lateinit var notificationService: NotificationService

    @Autowired
    lateinit var communityRepo: CommunityRepo

    @Autowired
    lateinit var memberRepo: MemberRepo

    @Autowired
    lateinit var joinRequestRepo: JoinRequestRepo

    @Autowired
    lateinit var securityGuardRepo: SecurityGuardRepo

    @Autowired
    lateinit var bookingRepo: BookingRepo

    @Value("\${visitor.access-code.length}")
    val accessCodeLength = 1

    private val logger = LoggerFactory.getLogger(CommunityService::class.java)





    /**
     * @param admin Admin of the community
     * @param requestId
     * @param accept      if the request was accepted or rejected
     * @return status of the request
     */
    @Transactional
    fun handleCommunityJoinRequest(
        admin: Admin,
        requestId: String,
        accept: Boolean
    ): ResponseEntity<*> {

        val community = communityRepo.findCommunityByIdAndAdminsContains(admin.communityId!!, admin.id!!)
            ?: return ResponseEntity.badRequest()
                .body(ApiResponseMessage.USER_NOT_SUPER_ADMIN)

        try {
            val joinRequest = joinRequestRepo.findById(requestId).orElseThrow()!!

            joinRequest.acceptedAt?.let {
                return ResponseEntity.accepted()
                    .body(ApiResponseMessage.REQUEST_ALREADY_ACCEPTED)
            }

            // Admin rejects request
            if (!accept) {
                joinRequestRepo.deleteById(requestId)
                //TODO:Notify referrer of rejection
                return ResponseEntity.ok("Request rejected")
            }

            // Update or create a Member
            val memberPhone = joinRequest.phone
            var member = memberRepo.findByPhone(memberPhone) ?: Member()

            member.firstName = joinRequest.firstName
            member.lastName = joinRequest.lastName
            member.communityId = community.id
            member.phone = memberPhone
            member = memberRepo.save(member)

            community.members = community.members.plus(member.id!!)
            communityRepo.save(community)

            joinRequest.acceptedAt = Date()
            joinRequest.approvedBy = admin.id
            joinRequestRepo.save(joinRequest)

            //TODO:Notify referrer of acceptance
            return ResponseEntity.ok(member)
        } catch (_: NoSuchElementException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseMessage.REQUEST_CANT_BE_FOUND)
        }
    }


    @Transactional
    fun inviteUserToCommunity(request: JoinRequest, referrer: Member): ResponseEntity<*> {
        try {

            val community = referrer.communityId?.let { communityRepo.findById(it).orElse(null) }
                ?: return ResponseEntity.badRequest()
                    .body("Referrer is not part of any community at this time")

            if (community.id != request.communityId) {
                return ResponseEntity.badRequest()
                    .body("Referrer can only invite a user to their community")
            }

            if (community.superAdminId == null) {
                return ResponseEntity.badRequest().body("Community does not have an super admin")
            }

            if (memberRepo.existsByPhoneAndCommunityIdIsNotNull(request.phone)) {
                return ResponseEntity.badRequest().body("User is already part of a community")
            }

            if (joinRequestRepo.existsByPhoneAndCommunityId(request.phone, request.communityId)) {
                return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("User already has a pending request")
            }

            request.referrerId = referrer.id
            joinRequestRepo.save(request)

            //TODO: Notify community admin
            return ResponseEntity.ok("Request to join community sent")

        } catch (_: NoSuchElementException) {
            return ResponseEntity.badRequest().body("Referrer not found")
        }
    }

    @Transactional
    fun addSecurityGuardToCommunity(guardData: GuardInfoBody, userId: String): ResponseEntity<*> {

        val community = communityRepo.findByAdminsContains(userId)
            ?: return ResponseEntity.badRequest().body("User not an Admin in a community")

        val phone = guardData.phone
        if (securityGuardRepo.existsByPhone(phone)) {
            ResponseEntity.badRequest().body("$phone is already a Guard in a community")
        }

        val guard = SecurityGuard()
        guard.phone = guardData.phone
        guard.firstName = guardData.firstName
        guard.lastName = guardData.lastName
        guard.communityId = community.id
        securityGuardRepo.save(guard)

        community.guards = community.guards.plus(guard.id!!)
        communityRepo.save(community)

        return ResponseEntity.ok().body(guardData)
    }

    @Transactional
    fun removeSecurityGuardFromCommunity(guardId: String, userId: String): ResponseEntity<*> {

        val community = communityRepo.findByAdminsContains(userId)
            ?: return ResponseEntity.badRequest().body("User not an Admin in a community")

        try {
            val guardCommunityId = securityGuardRepo.findById(guardId).orElseThrow()!!.communityId

            if (guardCommunityId == community.id) {

                securityGuardRepo.deleteById(guardId)
                community.guards = community.guards.minus(guardId)
                communityRepo.save(community)

                FirebaseAuth.getInstance().revokeRefreshTokens(guardId)
                return ResponseEntity.ok("Guard has been removed from community")
            } else {
                return ResponseEntity.badRequest().body("User not an Admin in Guard's community")
            }

        } catch (e: NoSuchElementException) {
            logger.warn(e.localizedMessage)
            return ResponseEntity.badRequest().body(e.localizedMessage)
        }
    }

}
