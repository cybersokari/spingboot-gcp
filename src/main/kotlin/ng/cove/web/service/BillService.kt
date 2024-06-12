package ng.cove.web.service

import com.mongodb.DuplicateKeyException
import ng.cove.web.data.model.*
import ng.cove.web.data.repo.*
import ng.cove.web.http.body.IssuableBillBody
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import java.util.*

@Service
class BillService {

    @Autowired
    private lateinit var communityRepo: CommunityRepo

    @Autowired
    private lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    lateinit var billRepo: BillRepo

    @Autowired
    lateinit var issuedBillRepo: IssuedBillRepo

    @Autowired
    lateinit var billPaymentRepo: BillPaymentRepo

    @Autowired
    lateinit var memberRepo: MemberRepo

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    fun createBill(bill: Bill, admin: Admin): ResponseEntity<*> {
        bill.communityId = admin.communityId

        return try {
            ResponseEntity.ok(billRepo.save(bill))
        } catch (e: DuplicateKeyException) {
            ResponseEntity.badRequest().body("Levy with this name already exists")
        }
    }

    fun issueBill(issuable: IssuableBillBody, admin: Admin): ResponseEntity<*> {
        val members = issuable.members
        try {

            val communityBill = billRepo.findById(issuable.billId!!).orElseThrow()
            if (communityBill.communityId != admin.communityId) {
                return ResponseEntity.badRequest().body("You are not an admin of this community")
            }

            // Ensure all members are in the same community
            if (members != null) {
                val community = communityRepo.findById(admin.communityId!!).orElseThrow()
                if (!community.members.containsAll(members)) {
                    return ResponseEntity.badRequest().body("Cannot assign bill to members not in this community")
                }
            }

        } catch (e: NoSuchElementException) {
            return ResponseEntity.badRequest().body(mapOf("message" to "Bill or member not found"))
        }

        val issuedBill = IssuedBill()
        issuedBill.issuedBy = admin.id
        return try {
            ResponseEntity.ok(issuedBillRepo.save(issuedBill))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("message" to "bill already issued to this member"))
        }
    }

    /** Each payment creation runs in this transaction so that the failure of one
    does not affect the creation of others from [createLevyPayments]*/
    @Transactional
    fun createPaymentForAssignedLevy(issuedBill: IssuedBill) {
        logger.info("Creating payment for member: ${issuedBill.memberId}")
        val payment = BillPayment().apply {
            billId = issuedBill.billId
            memberId = issuedBill.memberId
            dueDate = issuedBill.nextPaymentDue
        }
        billPaymentRepo.save(payment)

        //TODO: Notify member of payment

        // Update next payment due of assigned bill
        val cal = Calendar.getInstance().apply {
            time = issuedBill.nextPaymentDue
        }

        try {
            val bill = billRepo.findById(issuedBill.billId!!).orElseThrow()
            logger.info("Levy ${bill.title} current due date: ${issuedBill.nextPaymentDue}")

            when (bill.type!!) {
                BillType.MONTHLY -> {
                    cal.add(Calendar.MONTH, 1)
                    issuedBill.nextPaymentDue = cal.time
                }

                BillType.ANNUAL -> {
                    cal.add(Calendar.YEAR, 1)
                    issuedBill.nextPaymentDue = cal.time
                }

                BillType.ONCE -> issuedBill.nextPaymentDue = null
            }
            issuedBillRepo.save(issuedBill)
            logger.info("Levy ${bill.title} due date updated to: ${issuedBill.nextPaymentDue}")
        } catch (e: Exception) {
            logger.error("Bill created failed", e)
        }
    }

    @Scheduled(fixedDelayString = "\${schedule-bill-duration-secs}")
    fun createLevyPayments() {
        val duePayments = issuedBillRepo.findAllByNextPaymentDueIsBeforeOrderByNextPaymentDueAsc(Date())
        duePayments.forEach {
            // Use context to avoid Transaction self invocation
            webApplicationContext.getBean(this::class.java).createPaymentForAssignedLevy(it)
        }

    }
}