package ng.cove.web.service

import ng.cove.web.AppTest
import ng.cove.web.data.model.Community
import ng.cove.web.data.model.Levy
import ng.cove.web.data.model.LevyType
import ng.cove.web.data.model.Member
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import java.util.*


class LevyServiceTest : AppTest() {

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
}