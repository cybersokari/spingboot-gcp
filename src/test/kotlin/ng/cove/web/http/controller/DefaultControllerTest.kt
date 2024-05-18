package ng.cove.web.http.controller

import ng.cove.web.AppTests
import ng.cove.web.http.body.OtpRefBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import java.time.Instant
import java.time.ZoneId
import java.util.*


//@WebAppConfiguration("")
class DefaultControllerTest : AppTests() {

    @Test
    fun givenPhoneRegistered_whenUserPhoneGetOtp_thenSuccess() {
        val phone = member.phone!!
        Mockito.`when`(memberRepo.findByPhoneAndCommunityIsNotNull(phone)).thenReturn(member)
        val now = Date()
        Mockito.`when`(memberPhoneOtpRepo.countByCreatedAtIsAfter(now)).thenReturn(1)
        val otpRefBody = OtpRefBody("", phone, now, 2)
        Mockito.`when`(smsOtpService.sendOtp(member.phone!!)).thenReturn(otpRefBody)

        val result = mockMvc.get("/user/login?phone={phone}", member.phone!!).andReturn().response

        assertTrue(result.status == 200)
        verify(memberRepo, times(1)).findByPhoneAndCommunityIsNotNull(any())
        verify(memberPhoneOtpRepo, times(1)).countByCreatedAtIsAfter(any())
        verify(smsOtpService, times(1)).sendOtp(phone)
    }

    @Test
    fun givenPhoneNotRegistered_whenUserPhoneGetOtp_thenError() {
        val phone = member.phone!!
        Mockito.`when`(memberRepo.findByPhoneAndCommunityIsNotNull(phone)).thenReturn(null)
        val now = Date()
        Mockito.`when`(memberPhoneOtpRepo.countByCreatedAtIsAfter(now)).thenReturn(1)
        val otpRefBody = OtpRefBody("", phone, now, 2)
        Mockito.`when`(smsOtpService.sendOtp(member.phone!!)).thenReturn(otpRefBody)


        val result = mockMvc.perform(
            get("/user/login").param("phone", phone)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andReturn().response

        println("Test result: ${result.contentAsString}")
        assertTrue(result.status == 400, "Should return 400")
    }

    @Test
    fun givenDailyOtpLimitReached_whenUserPhoneGetOtp_thenError() {
        val phone = member.phone!!
        Mockito.`when`(memberRepo.findByPhoneAndCommunityIsNotNull(phone)).thenReturn(member)

        val aDayAgo = Date.from(Instant.from(dateTime.minusHours(24).atZone(ZoneId.systemDefault())))
        Mockito.`when`(memberPhoneOtpRepo.countByCreatedAtIsAfter(aDayAgo)).thenReturn(maxDailyOtpTrial.toLong())

        val result = mockMvc.get("/user/login?phone={phone}", member.phone!!).andReturn().response

        assertTrue(result.status == 400, "Should return 400")
        verify(memberPhoneOtpRepo, times(1)).countByCreatedAtIsAfter(any())
    }


    @Test
    fun givenValidOtp_whenLoginVerifyOtp_return200() {
        val phone = member.phone!!
        val otp = faker.number().randomNumber(6, true).toString()
        val ref = faker.random().hex(15)
        val token = faker.random().hex(55)
        Mockito.`when`(smsOtpService.verifyOtp(otp, ref)).thenReturn(phone)
        Mockito.`when`(memberRepo.findByPhone(phone)).thenReturn(member)


        Mockito.`when`(auth.createCustomToken(member.id!!, mapOf("type" to "Member")))
            .thenReturn(token)

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
        assertEquals(token, result.contentAsString)

        verify(smsOtpService, times(1)).verifyOtp(otp, ref)
        verify(memberRepo, times(1)).findByPhone(phone)
        verify(auth, times(1)).createCustomToken(any(), any())
    }
}