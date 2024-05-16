package ng.cove.web.http.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.auth.FirebaseAuth
import ng.cove.web.App
import ng.cove.web.AppTests
import ng.cove.web.component.SmsOtpService
import ng.cove.web.data.model.Community
import ng.cove.web.data.model.Member
import ng.cove.web.http.body.OtpRefBody
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.Mockito.mockStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.mock.web.MockServletContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*


@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [App::class])
@WebAppConfiguration("")
@ActiveProfiles("test")
class DefaultControllerTest : AppTests() {

    private lateinit var member: Member
    private lateinit var community: Community

    @Autowired
    lateinit var webApplicationContext: WebApplicationContext

    @MockBean
    lateinit var smsOtpService: SmsOtpService

    lateinit var mockMvc: MockMvc

    lateinit var staticFirebaseAuth: MockedStatic<FirebaseAuth>
    lateinit var staticLocalDateTime: MockedStatic<LocalDateTime>


    val auth: FirebaseAuth = Mockito.mock(FirebaseAuth::class.java)

    // Fixed date for testing
    private final val instantExpected: String = "2024-05-22T10:15:30Z"
    private final var clock: Clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.systemDefault())
    var dateTime: LocalDateTime = LocalDateTime.now(clock)

    @Value("\${otp.trial-limit}")
    var maxDailyOtpTrial: Int = 0


    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()

        community = Community()
        community.id = "2133232323423"
        community.name = "Test Community"
        community.address = "123 Test Street"

        member = Member()
        member.id = "user_id"
        member.firstName = "Janet"
        member.lastName = "Doe"
        member.phone = "23481234567"

        community.superAdminId = member.id
        community.adminIds = setOf(member.id!!)

        member.community = community

        // Mock LocalDateTime
        staticLocalDateTime = mockStatic(LocalDateTime::class.java)
        staticLocalDateTime.`when`<LocalDateTime>(LocalDateTime::now).thenReturn(dateTime)
        // Mock FirebaseAuth
        staticFirebaseAuth = mockStatic(FirebaseAuth::class.java)
        staticFirebaseAuth.`when`<Any>(FirebaseAuth::getInstance).thenReturn(auth)

    }

    @AfterEach
    fun tearDown() {
        staticFirebaseAuth.close()
        staticLocalDateTime.close()
    }

    @Test
    fun givenWac_whenServletContext_thenItProvidesDefaultController() {
        val servletContext = webApplicationContext.servletContext
        assertNotNull(servletContext)
        assertTrue(servletContext is MockServletContext)
        assertNotNull(webApplicationContext.getBean(DefaultController::class.java))
    }

    @Test
    fun validUserPhoneGetOtp() {
        val phone = member.phone!!
        Mockito.`when`(memberRepo.findByPhoneAndCommunityIsNotNull(phone)).thenReturn(member)
        val now = Date()
        Mockito.`when`(memberPhoneOtpRepo.countByCreatedAtIsAfter(now)).thenReturn(1)
        val otpRefBody = OtpRefBody("", phone, now, 2)
        Mockito.`when`(smsOtpService.sendOtp(member.phone!!)).thenReturn(otpRefBody)

        val result = mockMvc.get("/user/login?phone={phone}", member.phone!!).andReturn().response
        println("Test result: ${result.contentAsString}")
        assertTrue(result.status == 200)
    }

    @Test
    fun unknownUserPhoneGetError() {
        val phone = member.phone!!
        Mockito.`when`(memberRepo.findByPhoneAndCommunityIsNotNull(phone)).thenReturn(null)
        val now = Date()
        Mockito.`when`(memberPhoneOtpRepo.countByCreatedAtIsAfter(now)).thenReturn(1)
        val otpRefBody = OtpRefBody("", phone, now, 2)
        Mockito.`when`(smsOtpService.sendOtp(member.phone!!)).thenReturn(otpRefBody)

        val result = mockMvc.get("/user/login?phone={phone}", member.phone!!).andReturn().response
        assertTrue(result.status == 400, "Should return 400")
    }

    @Test
    fun otpLimitExceededError() {
        val phone = member.phone!!
        Mockito.`when`(memberRepo.findByPhoneAndCommunityIsNotNull(phone)).thenReturn(member)

        val aDayAgo = Date.from(Instant.from(dateTime.minusHours(24).atZone(ZoneId.systemDefault())))
        Mockito.`when`(memberPhoneOtpRepo.countByCreatedAtIsAfter(aDayAgo)).thenReturn(maxDailyOtpTrial.toLong())

        val result = mockMvc.get("/user/login?phone={phone}", member.phone!!).andReturn().response

        assertTrue(result.status == 400, "Should return 400")
    }


    @Test
    fun verifyUserPhoneOtp() {
        val phone = member.phone!!
        val otp = "223355"
        val ref = "wew342342432"
        val token = "test_token"
        Mockito.`when`(smsOtpService.verifyOtp(otp, ref)).thenReturn(phone)
        Mockito.`when`(memberRepo.findByPhone(phone)).thenReturn(member)


        Mockito.`when`(auth.createCustomToken(member.id!!, mapOf("type" to "Member")))
            .thenReturn(token)

        val login = mapOf(
            "phone" to phone,
            "otp" to otp,
            "ref" to ref,
            "device_id" to "device_id",
            "device_name" to "Samsung Galaxy S20"
        )
        val result = mockMvc.post("/user/login/verify") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(login)
        }.andReturn().response

        assertEquals(token, result.contentAsString)

    }

    @Test
    fun loginSecurityGuard() {
    }

    @Test
    fun verifyGuardPhoneOtp() {
    }
}