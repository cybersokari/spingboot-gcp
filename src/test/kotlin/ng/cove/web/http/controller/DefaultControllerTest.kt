package ng.cove.web.http.controller

import ng.cove.web.AppTest
import ng.cove.web.data.model.PhoneOtp
import ng.cove.web.data.model.UserType
import ng.cove.web.http.body.OtpRefBody
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.springframework.http.MediaType
import org.springframework.test.context.event.annotation.AfterTestClass
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import java.time.Duration
import java.time.Instant
import java.util.*


//@WebAppConfiguration("")
class DefaultControllerTest : AppTest() {

    @Test
    fun givenPhoneRegistered_whenUserPhoneGetOtp_thenSuccess() {
        val phone = member.phone!!
        memberRepo.save(member)
        val ref = faker.random().hex(20)
        val phoneOtp = PhoneOtp()
        phoneOtp.phone = phone
        phoneOtp.ref = ref
        phoneOtp.type = UserType.Member
        phoneOtp.expireAt = Date()
        memberPhoneOtpRepo.save(phoneOtp)
        val otpRefBody = OtpRefBody(ref, phone, Date(), 2)
        Mockito.`when`(smsOtpService.sendOtp(member.phone!!)).thenReturn(otpRefBody)

        val result = mockMvc.get("/user/login?phone={phone}", member.phone!!).andReturn().response

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
        phoneOtp.type = UserType.Member
        phoneOtp.expireAt = Date()
        memberPhoneOtpRepo.save(phoneOtp)
        val otpRefBody = OtpRefBody(ref, phone, Date(), 2)
        Mockito.`when`(smsOtpService.sendOtp(member.phone!!)).thenReturn(otpRefBody)


        val result = mockMvc.perform(
            get("/user/login").param("phone", phone)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andReturn().response

        println("Test result: ${result.contentAsString}")
        assertTrue(result.status == 400, "Should return 400")
    }

    @Nested
    inner class OTPVerificationTest{

        @BeforeEach
        fun setUp(){
            memberRepo.save(member)
            val phoneOtps = buildList {
                repeat(maxDailyOtpTrial) {
                    val momentsAgo = Instant.now().minus(Duration.ofHours(faker.random().nextLong(1, 8)))
                    val phoneOtp = PhoneOtp()
                    phoneOtp.phone = member.phone!!
                    phoneOtp.ref = faker.random().hex(20)
                    phoneOtp.type = UserType.Member
                    phoneOtp.expireAt = Date.from(momentsAgo)
                    add(phoneOtp)
                }
            }
            memberPhoneOtpRepo.saveAll(phoneOtps)
        }
        @AfterEach
        fun tearDown(){
            memberPhoneOtpRepo.deleteAllByPhone(member.phone!!)
        }
        @Test
        fun givenDailyOtpLimitReached_whenUserPhoneGetOtp_thenError() {

            val result = mockMvc.get("/user/login?phone={phone}", member.phone!!).andReturn().response

            assertEquals(400, result.status, "Should return 400")
            verifyNoInteractions(smsOtpService)
        }


        @Test
        fun givenValidOtp_whenLoginVerifyOtp_return200() {

            val phone = member.phone!!
            val otp = faker.number().randomNumber(6, true).toString()
            val ref = faker.random().hex(15)
            Mockito.`when`(smsOtpService.verifyOtp(otp, ref)).thenReturn(phone)

            val customJWT = faker.random().hex(55)
            Mockito.`when`(auth.createCustomToken(member.id!!, mapOf("type" to "Member")))
                .thenReturn(customJWT)

            val login = mapOf(
                "otp" to otp,
                "ref" to ref,
                "device_id" to faker.random().hex(30),
                "device_name" to faker.device().modelName()
            )
            val result = mockMvc.post("/user/login/verify") {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(login)
            }.andReturn().response

            assertEquals(200, result.status)
            assertEquals(customJWT, result.contentAsString, "Custom JWT is returned")
            assertEquals(0, memberPhoneOtpRepo.countByPhone(phone), "OTP limit data is cleared")

            verify(smsOtpService, times(1)).verifyOtp(otp, ref)
            verify(auth, times(1)).createCustomToken(any(), any())
        }
    }


}