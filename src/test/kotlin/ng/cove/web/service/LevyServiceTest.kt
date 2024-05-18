package ng.cove.web.service

import ng.cove.web.AppTests
import ng.cove.web.data.model.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import java.util.*


class LevyServiceTest : AppTests() {

    @Autowired
    private lateinit var levyService: LevyService

    private lateinit var admin: Member
    private lateinit var createdLevy: Levy
    private lateinit var levyData: Levy


    @BeforeEach
    override fun setUp() {
        super.setUp()
        community = Community()
        community.id = "2133232323423"
        community.name = "Test Community"
        community.address = "123 Test Street"

        admin = Member()
        admin.id = "admin_user_id"
        admin.firstName = "Janet"
        admin.lastName = "Doe"
        admin.phone = "234234234234"

        community.superAdminId = admin.id
        community.adminIds = setOf(admin.id!!)

        admin.community = community

        levyData = Levy()
        levyData.title = "Apartments"
        levyData.communityId = community.id
        levyData.type = LevyType.Annual
        levyData.amount = 1200000.0

        createdLevy = levyData
        createdLevy.createdAt = Calendar.getInstance().time
        createdLevy.updatedAt = Calendar.getInstance().time
        createdLevy.id = "levy_id"
    }



    @Test
    fun createLevy() {

        Mockito.`when`(levyRepo.save(levyData)).thenReturn(createdLevy)

        assertEquals(ResponseEntity.ok(createdLevy), levyService.createLevy(levyData, admin))
    }

    @Test
    fun assignLevy() {
        val levyToAssign = AssignedLevy()
        levyToAssign.levyId = createdLevy.id!!
        levyToAssign.memberId = admin.id

        Mockito.`when`(levyRepo.findById(levyToAssign.levyId!!)).thenReturn(Optional.of(createdLevy))

        Mockito.`when`(memberRepo.findById(levyToAssign.memberId!!)).thenReturn(Optional.of(admin))

        Mockito.`when`(assignedLevyRepo.save(levyToAssign)).thenReturn(levyToAssign)

        assertEquals(ResponseEntity.ok(levyToAssign), levyService.assignLevy(levyToAssign, admin))

    }

    @Test
    fun dueLevyCreatesPayment() {
        levyPaymentRepo
    }
}