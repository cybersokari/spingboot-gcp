package ng.cove.web.http.controller

import ng.cove.web.AppTest
import ng.cove.web.data.model.PhoneOtp
import ng.cove.web.data.model.UserType
import ng.cove.web.http.body.LoginBody
import ng.cove.web.http.body.OtpRefBody
import ng.cove.web.service.SmsOtpService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import java.time.Duration
import java.time.Instant
import java.util.*


class DefaultControllerTest : AppTest() {

    @MockBean
    lateinit var smsOtpService: SmsOtpService

    @Value("\${otp-trial-limit}")
    var maxDailyOtpTrial: Int = 0

    @BeforeEach
    override fun setUp() {
        super.setUp()
        reset(smsOtpService)
        reset(auth)
    }

    @Test
    fun givenPhoneRegistered_whenUserPhoneGetOtp_thenSuccess() {
        val phone = member.phone!!
        memberRepo.save(member)
        val ref = faker.random().hex(20)
        val phoneOtp = PhoneOtp()
        phoneOtp.phone = phone
        phoneOtp.ref = ref
        phoneOtp.type = UserType.MEMBER
        phoneOtp.expireAt = Date()
        memberPhoneOtpRepo.save(phoneOtp)
        val otpRefBody = OtpRefBody(ref, phone, Date(), 2)
        `when`(smsOtpService.sendOtp(phone)).thenReturn(otpRefBody)

        val result = mockMvc.get(
            "/login?phone={phone}&type={type}",
            phone, UserType.MEMBER
        ).andReturn().response

        assertTrue(result.status == 200)
        verify(smsOtpService, times(1)).sendOtp(phone)
    }

    @Test
    fun givenPhoneNotRegistered_whenUserPhoneGetOtp_thenError() {
        val phone = member.phone!!
        val ref = faker.random().hex(20)
        val phoneOtp = PhoneOtp()
        phoneOtp.phone = phone
        phoneOtp.ref = ref
        phoneOtp.type = UserType.MEMBER
        phoneOtp.expireAt = Date()
        memberPhoneOtpRepo.save(phoneOtp)
        val otpRefBody = OtpRefBody(ref, phone, Date(), 2)
        `when`(smsOtpService.sendOtp(member.phone!!)).thenReturn(otpRefBody)

        val result = mockMvc.perform(
            get("/login").param("phone", phone)
                .param("type", UserType.MEMBER.name)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andReturn().response

        assertTrue(result.status == 400, "Should return 400")
    }

    @Test
    fun givenUserIsTester_whenLoginWithPhone_ThenDoNotSendOtp() {
        member.testOtp = faker.random().nextInt(6).toString()
        memberRepo.save(member)

        val result = mockMvc.perform(
            get("/login").param("phone", member.phone!!)
                .param("type", UserType.MEMBER.name)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andReturn().response
        assertEquals(200, result.status)
        verifyNoInteractions(smsOtpService)
    }

    @Nested
    inner class OTPVerificationTest {

        @BeforeEach
        fun setUp() {
            memberRepo.save(member)
            val phoneOtps = buildList {
                repeat(maxDailyOtpTrial) {
                    val momentsAgo = Instant.now().minus(Duration.ofHours(faker.random().nextLong(1, 8)))
                    val phoneOtp = PhoneOtp()
                    phoneOtp.phone = member.phone!!
                    phoneOtp.ref = faker.random().hex(20)
                    phoneOtp.type = UserType.MEMBER
                    phoneOtp.expireAt = Date.from(momentsAgo)
                    add(phoneOtp)
                }
            }
            memberPhoneOtpRepo.saveAll(phoneOtps)
        }

        @AfterEach
        fun tearDown() {
            memberPhoneOtpRepo.deleteAllByPhone(member.phone!!)
        }

        @Test
        fun givenDailyOtpLimitReached_whenUserPhoneGetOtp_thenError() {

            val result = mockMvc.get(
                "/login?phone={phone}&type={type}",
                member.phone!!, UserType.MEMBER
            ).andReturn().response

            assertEquals(400, result.status, "Should return 400")
            verifyNoInteractions(smsOtpService)
        }


        @Test
        fun givenValidOtp_whenLoginVerifyOtp_return200() {

            val phone = member.phone!!
            val otp = faker.number().randomNumber(6, true).toString()
            val ref = faker.random().hex(15)
            `when`(smsOtpService.verifyOtp(otp, ref)).thenReturn(phone)

            val customJWT = faker.random().hex(55)
            `when`(auth.createCustomToken(member.id!!, mapOf("type" to UserType.MEMBER.name)))
                .thenReturn(customJWT)

            val login = LoginBody().apply {
                this.type = UserType.MEMBER
                this.otp = otp
                this.ref = ref
                this.deviceName = faker.device().modelName()
            }
            val result = mockMvc.post("/login/verify") {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(login)
            }.andReturn().response

            assertEquals(200, result.status)
            assertEquals(customJWT, result.contentAsString, "Custom JWT is returned")
            assertEquals(0, memberPhoneOtpRepo.countByPhone(phone), "OTP limit data is cleared")

            verify(smsOtpService, times(1)).verifyOtp(otp, ref)
            verify(auth, times(1)).createCustomToken(any(), any())
        }

        @Test
        fun givenUserIsTester_whenVerifyOtp_ThenDoNotCallVerificationService() {
            val otp = faker.random().nextInt(6).toString()
            member.testOtp = otp
            memberRepo.save(member)

            val customJWT = faker.random().hex(55)
            `when`(auth.createCustomToken(member.id!!, mapOf("type" to "Member")))
                .thenReturn(customJWT)


            val login = LoginBody().apply {
                this.type = UserType.MEMBER
                this.otp = otp
                this.ref = member.id!!
                this.deviceName = faker.device().modelName()
            }

            val result = mockMvc.post("/login/verify") {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(login)
            }.andReturn().response

            assertEquals(200, result.status)
            verifyNoInteractions(smsOtpService)
        }

        @Test
        fun givenRouteIsNotConfigured_whenRouteIsCalled_thenReturn404() {
            val result = mockMvc.post("/").andReturn().response
            assertTrue(result.status == 404)
        }
    }


}