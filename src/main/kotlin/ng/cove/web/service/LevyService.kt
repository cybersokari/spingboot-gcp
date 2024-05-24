package ng.cove.web.service

import com.mongodb.DuplicateKeyException
import ng.cove.web.data.model.*
import ng.cove.web.data.repo.AssignedLevyRepo
import ng.cove.web.data.repo.LevyPaymentRepo
import ng.cove.web.data.repo.LevyRepo
import ng.cove.web.data.repo.MemberRepo
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import java.util.*

@Service
class LevyService {

    @Autowired
    private lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    lateinit var levyRepo: LevyRepo

    @Autowired
    lateinit var assignedLevyRepo: AssignedLevyRepo

    @Autowired
    lateinit var levyPaymentRepo: LevyPaymentRepo

    @Autowired
    lateinit var memberRepo: MemberRepo

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    fun createLevy(levy: Levy, admin: Member): ResponseEntity<*> {
        levy.communityId = admin.community!!.id

        return try {
            ResponseEntity.ok(levyRepo.save(levy))
        } catch (e: DuplicateKeyException) {
            ResponseEntity.badRequest().body("Levy with this name already exists")
        }
    }

    fun assignLevy(levyToAssign: AssignedLevy, admin: Member): ResponseEntity<*> {
        try {
            val communityLevy = levyRepo.findById(levyToAssign.levyId!!).orElseThrow()
            if (communityLevy.communityId != admin.community!!.id) {
                return ResponseEntity.badRequest().body("You are not an admin of this community")
            }

            val member: Member = memberRepo.findById(levyToAssign.memberId!!).orElseThrow()
            if (communityLevy.communityId != member.community!!.id) {
                return ResponseEntity.badRequest().body("Cannot assign levy from another community")
            }
        } catch (e: NoSuchElementException) {
            return ResponseEntity.badRequest().body(mapOf("message" to "Levy or member not found"))
        }

        levyToAssign.assignedBy = admin.id
        return try {
            ResponseEntity.ok(assignedLevyRepo.save(levyToAssign))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("message" to "levy already assigned to this member"))
        }
    }

    /** Each payment creation runs in this transaction so that the failure of one
    does not affect the creation of others from [createLevyPayments]*/
    @Transactional
    fun createPaymentForAssignedLevy(assignedLevy: AssignedLevy) {
        logger.info("Creating payment for ${assignedLevy.memberId}")
        val payment = LevyPayment()
        payment.levyId = assignedLevy.levyId
        payment.memberId = assignedLevy.memberId
        payment.dueDate = assignedLevy.nextPaymentDue
        levyPaymentRepo.save(payment)

        //TODO: Notify member of payment

        // Update next payment due of assigned levy
        val levy = levyRepo.findById(assignedLevy.levyId!!).orElseThrow()
        val cal = Calendar.getInstance()
        logger.info("Levy ${levy.title} current due date:${assignedLevy.nextPaymentDue}")
        cal.time = assignedLevy.nextPaymentDue
        when (levy.type!!) {
            LevyType.Month -> {
                cal.add(Calendar.MONTH, 1)
            }

            LevyType.Annual -> {
                cal.add(Calendar.YEAR, 1)
            }
        }
        assignedLevy.nextPaymentDue = cal.time
        assignedLevyRepo.save(assignedLevy)
        logger.info("Levy ${levy.title} due date updated to: ${assignedLevy.nextPaymentDue}")
    }

    @Scheduled(fixedDelayString = "\${schedule-levy-duration-secs}")
    fun createLevyPayments() {
        val duePayments = assignedLevyRepo.findAllByNextPaymentDueIsBeforeOrderByNextPaymentDueAsc(Date())
        duePayments.forEach {
            // Use context to avoid Transaction self invocation
            webApplicationContext.getBean(this::class.java).createPaymentForAssignedLevy(it)
        }

    }
}