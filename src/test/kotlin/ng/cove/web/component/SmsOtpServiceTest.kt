package ng.cove.web.component

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.http.ResponseEntity
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.client.RestTemplate
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ExtendWith(SpringExtension::class)
class SmsOtpServiceTest {

    @Mock
    lateinit var restTemplate: RestTemplate

    private lateinit var service: SmsOtpService

    private val phone = "23458689595"

    @BeforeEach
    fun setUp() {
        service = SmsOtpService(restTemplate)
    }

    @Test
    fun givenValidPhone_whenSendOtp_ReturnSuccess() {

        val ref = "12234344"
        val apiResult = mapOf("status" to 200, "pinId" to ref)

        val mockResponse = ResponseEntity.ok().body<Map<*, *>>(apiResult)
        `when`(restTemplate.postForEntity(any<String>(), any(), any<Class<*>>())).thenReturn(mockResponse)

        val result = service.sendOtp(phone)

        assertNotNull(result)
        assertEquals(ref, result.ref, "Otp reference from API is return")
        assertTrue(result.expireAt.toInstant().isAfter(Instant.now()), "ExpireAt is in the future")
        verify(restTemplate, times(1)).postForEntity(any<String>(), any(), any<Class<*>>())
        verifyNoMoreInteractions(restTemplate)
    }

    @Test
    fun givenValidOtpRef_whenVerifyOtp_ReturnSuccess() {

        val apiResult = mapOf("status" to 200, "msisdn" to phone)

        val mockResponse = ResponseEntity.ok(apiResult)
        `when`(restTemplate.postForEntity(any<String>(), any(), any<Class<*>>())).thenReturn(mockResponse)

        val ref = "12234344"
        val result = service.verifyOtp("", ref)

        assertNotNull(result)
        assertEquals(phone, result, "Correct phone number is return")
        verify(restTemplate, times(1)).postForEntity(any<String>(), any(), any<Class<*>>())
        verifyNoMoreInteractions(restTemplate)
    }
}