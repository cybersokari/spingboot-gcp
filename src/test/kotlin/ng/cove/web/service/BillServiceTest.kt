package ng.cove.web.service

import ng.cove.web.AppTest
import ng.cove.web.data.model.IssuedBill
import ng.cove.web.data.model.Bill
import ng.cove.web.data.model.BillType
import ng.cove.web.data.repo.IssuedBillRepo
import ng.cove.web.data.repo.BillPaymentRepo
import ng.cove.web.data.repo.BillRepo
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.PropertiesPropertySource
import org.springframework.web.context.WebApplicationContext
import java.time.Duration
import java.time.Instant
import java.util.*
import kotlin.test.assertTrue


class BillServiceTest : AppTest() {

    @Autowired
    lateinit var billRepo: BillRepo

    @Autowired
    lateinit var issuedBillRepo: IssuedBillRepo

    @Autowired
    lateinit var billPaymentRepo: BillPaymentRepo

    lateinit var issuedBillNotDueForPayment: IssuedBill

    @Autowired
    lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    lateinit var environment : ConfigurableEnvironment

    val initialDelay = 2

    @BeforeEach
    override fun setUp() {
        super.setUp()
        var bill = Bill().apply {
            title = "Refuse dump fee"
            communityId = community.id
            amount = 23000.0
            type = BillType.MONTHLY
        }
        bill = billRepo.save(bill)
        issuedBillNotDueForPayment = IssuedBill().apply {
            billId = bill.id
            memberId = member.id
            issuedBy = member.id
            nextPaymentDue = Date.from(Instant.now().plus(Duration.ofDays(1)))
        }
        issuedBillNotDueForPayment = issuedBillRepo.save(issuedBillNotDueForPayment)

        val prop = Properties().apply {
            put("schedule-levy-duration-secs", initialDelay)
        }
        environment.propertySources.addFirst(PropertiesPropertySource("test",prop))
    }

    @AfterAll
    fun teardown() {
        billRepo.deleteAll()
        issuedBillRepo.deleteAll()
    }

    @Test
    fun givenThatAssignedLevyPaymentIsDue_whenSchedulerRuns_PaymentIsCreated() {

        assertTrue(billPaymentRepo.count().toInt() == 0, "No payment yet")

        // Backdating nextPaymentDue
        val aHourAgo = Date.from(Instant.now().minus(Duration.ofHours(1)))
        issuedBillNotDueForPayment.nextPaymentDue = aHourAgo
        issuedBillRepo.save(issuedBillNotDueForPayment)

        /** This [delayInSecs] value is updated by [AppTest]'s @SpringBootTest annotation to fast track
        the execution of this test. The production [delayInSecs] value is not affect*/
        val delayInSecs = webApplicationContext.environment.getProperty("schedule-levy-duration-secs")

        /** Waiting for [BillService.createLevyPayments]'s [@Scheduled] annotation
        to take effect */
        Thread.sleep(delayInSecs!!.toLong() * 1000)

        assertTrue(billPaymentRepo.count().toInt() > 0, "Payment created by Scheduled function")
        val createdPayment = issuedBillRepo.findAll().first()!!
        val now = Instant.now()
        assertTrue(
            createdPayment.nextPaymentDue!!.toInstant().isAfter(now),
            "Next payment updated to future"
        )
    }

}