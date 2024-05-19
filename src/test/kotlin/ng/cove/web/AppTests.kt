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
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.Mockito.mockStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc


@SpringBootTest(classes = [App::class])
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AppTests {

    @Autowired
    lateinit var communityRepo: CommunityRepo

    @Autowired
    lateinit var memberRepo: MemberRepo

    @Autowired
    lateinit var joinRequestRepo: JoinRequestRepo

    @Autowired
    lateinit var memberPhoneOtpRepo: MemberPhoneOtpRepo


    lateinit var member: Member
    lateinit var community: Community

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


        // Mock FirebaseAuth
        staticFirebaseAuth = mockStatic(FirebaseAuth::class.java)
        staticFirebaseAuth.`when`<FirebaseAuth>(FirebaseAuth::getInstance).thenReturn(auth)
    }

    @AfterEach
    fun tearDown() {
        staticFirebaseAuth.close()

//        communityRepo.deleteAll()
//        memberRepo.deleteAll()
//        joinRequestRepo.deleteAll()
//        memberPhoneOtpRepo.deleteAll()
    }



}
