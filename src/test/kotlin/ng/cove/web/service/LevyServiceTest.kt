package ng.cove.web.service

import ng.cove.web.AppTest
import ng.cove.web.data.model.AssignedLevy
import ng.cove.web.data.model.Levy
import ng.cove.web.data.model.LevyType
import ng.cove.web.data.repo.AssignedLevyRepo
import ng.cove.web.data.repo.LevyPaymentRepo
import ng.cove.web.data.repo.LevyRepo
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.context.WebApplicationContext
import java.time.Duration
import java.time.Instant
import java.util.*
import kotlin.test.assertTrue


class LevyServiceTest : AppTest() {

    @Autowired
    lateinit var levyRepo: LevyRepo

    @Autowired
    lateinit var assignedLevyRepo: AssignedLevyRepo

    @Autowired
    lateinit var levyPaymentRepo: LevyPaymentRepo

    lateinit var assignedLevyNotDueForPayment: AssignedLevy

    @Autowired
    lateinit var webApplicationContext: WebApplicationContext


    @BeforeEach
    override fun setUp() {
        super.setUp()
        var levy = Levy().apply {
            title = "Refuse dump fee"
            communityId = community.id
            amount = 23000.0
            type = LevyType.Month
        }
        levy = levyRepo.save(levy)
        assignedLevyNotDueForPayment = AssignedLevy().apply {
            levyId = levy.id
            memberId = member.id
            assignedBy = member.id
            nextPaymentDue = Date.from(Instant.now().plus(Duration.ofDays(1)))
        }
        assignedLevyNotDueForPayment = assignedLevyRepo.save(assignedLevyNotDueForPayment)
    }

    @AfterAll
    fun teardown() {
        levyRepo.deleteAll()
        assignedLevyRepo.deleteAll()
    }

    @Test
    fun givenThatAssignedLevyPaymentIsDue_whenSchedulerRuns_PaymentIsCreated() {

        assertTrue(levyPaymentRepo.count().toInt() == 0, "No payment yet")

        // Backdating nextPaymentDue
        val aHourAgo = Date.from(Instant.now().minus(Duration.ofHours(1)))
        assignedLevyNotDueForPayment.nextPaymentDue = aHourAgo
        assignedLevyRepo.save(assignedLevyNotDueForPayment)

        /** This [delayInSecs] value is updated by [AppTest]'s @SpringBootTest annotation to fast track
        the execution of this test. The production [delayInSecs] value is not affect*/
        val delayInSecs = webApplicationContext.environment.getProperty("schedule-levy-duration-secs")

        /** Waiting for [LevyService.createLevyPayments]'s [@Scheduled] annotation
        to take effect */
        Thread.sleep(delayInSecs!!.toLong() * 1000)

        assertTrue(levyPaymentRepo.count().toInt() > 0, "Payment created by Scheduled function")
        val createdPayment = assignedLevyRepo.findAll().first()!!
        val now = Instant.now()
        assertTrue(
            createdPayment.nextPaymentDue!!.toInstant().isAfter(now),
            "Next payment updated to future"
        )
    }

}