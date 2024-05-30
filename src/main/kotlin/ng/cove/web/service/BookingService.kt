package ng.cove.web.service

import com.mongodb.DuplicateKeyException
import ng.cove.web.data.model.Booking
import ng.cove.web.data.model.Member
import ng.cove.web.data.model.SecurityGuard
import ng.cove.web.data.repo.*
import ng.cove.web.http.body.ApiError
import ng.cove.web.util.AccessCodeGenerator
import ng.cove.web.util.ApiResponseMessage.ENTER_AFTER_MUST_BE_BEFORE_EXIT_BEFORE
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class BookingService {

    private val logger = LoggerFactory.getLogger(CommunityService::class.java)

    @Autowired
    private lateinit var notificationService: NotificationService

    @Autowired
    lateinit var memberRepo: MemberRepo


    @Autowired
    lateinit var bookingRepo: BookingRepo

    @Value("\${visitor.access-code.length}")
    val accessCodeLength = 1


    fun bookVisitor(bookingEntry: Booking, member: Member): ResponseEntity<*> {

        val timeOfEntry = bookingEntry.enterAfter.toInstant()
        val timeOfExit = bookingEntry.exitBefore.toInstant()

        return if (timeOfEntry.isBefore(timeOfExit)){
            val generator = AccessCodeGenerator()
            var booking: Booking = bookingEntry
            while (booking.id == null) {// Only saved booking will have Mongo generated ID
                booking.apply {
                    communityId = member.communityId
                    code = generator.getCode(accessCodeLength)
                    host = member.id
                }

                booking = try {
                    bookingRepo.save(booking)
                } catch (e: DuplicateKeyException) {
                    logger.warn("Access code duplicate conflict for member: ${member.communityId}")
                    booking
                }
            }
            ResponseEntity.ok(booking)
        } else {
        ResponseEntity.badRequest().body(ApiError(ENTER_AFTER_MUST_BE_BEFORE_EXIT_BEFORE))
        }
    }

    fun checkInVisitor(code: String, guard: SecurityGuard): ResponseEntity<Any> {
        try {

            var access = bookingRepo.findByCodeAndCommunityId(code, guard.communityId!!)
                ?: return ResponseEntity.noContent().build()

            if (access.checkedInAt == null) {
                access.checkedInAt = Date()
                access.checkedInBy = guard.id
            }
            access = bookingRepo.save(access) // Update access

            //TODO: Notify host of check in

            return ResponseEntity.ok().body(access)

        } catch (e: Exception) {
            logger.error(e.localizedMessage)
            return ResponseEntity.internalServerError().build()
        }
    }

    fun checkOutVisitor(code: String, guard: SecurityGuard): ResponseEntity<Any> {
        try {
            // Find checked-in booking
            var booking = bookingRepo.findByCodeAndCommunityIdAndCheckedInAtIsNotNull(code, guard.communityId!!)
                ?: return ResponseEntity.badRequest().body("Checked-in booking not found")

            if (booking.checkedInAt == null) {
                booking.checkedOutAt = Date()
                booking.checkedOutBy = guard.id
            }
            booking = bookingRepo.save(booking) // Update access

            //TODO: Notify host of check out

            return ResponseEntity.ok().body(booking)
        } catch (e: Exception) {
            logger.error(e.localizedMessage)
            return ResponseEntity.internalServerError().build()
        }
    }
}