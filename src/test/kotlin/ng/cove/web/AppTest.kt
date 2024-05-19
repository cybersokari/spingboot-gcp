package ng.cove.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.google.firebase.auth.FirebaseAuth
import net.datafaker.Faker
import ng.cove.web.component.SmsOtpService
import ng.cove.web.data.model.Community
import ng.cove.web.data.model.Member
import ng.cove.web.data.repo.CommunityRepo
import ng.cove.web.data.repo.JoinRequestRepo
import ng.cove.web.data.repo.MemberPhoneOtpRepo
import ng.cove.web.data.repo.MemberRepo
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.Mockito.mockStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc


@SpringBootTest(classes = [App::class])
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource("/application–test.properties")
class AppTest {

    @Autowired
    lateinit var communityRepo: CommunityRepo

    @Autowired
    lateinit var memberRepo: MemberRepo

    @Autowired
    lateinit var joinRequestRepo: JoinRequestRepo

    @Autowired
    lateinit var memberPhoneOtpRepo: MemberPhoneOtpRepo

    @MockBean
    lateinit var smsOtpService: SmsOtpService

    @Autowired
    lateinit var mockMvc: MockMvc


    lateinit var staticFirebaseAuth: MockedStatic<FirebaseAuth>

    // Mocked FirebaseAuth for testing
    val auth: FirebaseAuth = Mockito.mock(FirebaseAuth::class.java)

    @Value("\${otp.trial-limit}")
    var maxDailyOtpTrial: Int = 0

    final val faker = Faker()
    val mapper = ObjectMapper().apply { propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE }

    lateinit var member: Member
    lateinit var community: Community

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @BeforeEach
    fun setUp() {

        community = Community()
        community.id = faker.random().hex(20)
        community.name = "${faker.address().state()} Community"
        community.address = faker.address().streetAddress()

        member = Member()
        member.id = faker.random().hex(20)
        member.firstName = faker.name().firstName()
        member.lastName = faker.name().lastName()
        member.phone = faker.phoneNumber().cellPhone()

        community.superAdminId = member.id
        community.adminIds = setOf(member.id!!)

        member.community = community
    }

    @BeforeAll
    fun setupAll() {
        // Mock FirebaseAuth
        staticFirebaseAuth = mockStatic(FirebaseAuth::class.java)
        staticFirebaseAuth.`when`<FirebaseAuth>(FirebaseAuth::getInstance).thenReturn(auth)

        //TODO: Investigate why 'phone_otp' collection index is always duplicate in Embedded db
        mongoTemplate.collectionNames.forEach {
            mongoTemplate.getCollection(it).dropIndexes()
            mongoTemplate.getCollection(it).drop()
        }
    }

    @AfterAll
    fun tearDownAll() {
        staticFirebaseAuth.close()
    }

}
