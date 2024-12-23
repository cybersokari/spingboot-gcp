package ng.cove.web.service

import com.google.gson.JsonObject
import ng.cove.web.http.body.OtpRefBody
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.time.Instant
import java.util.*

const val SEND_URL = "https://api.ng.termii.com/api/sms/otp/send"
const val VERIFY_URL = "https://api.ng.termii.com/api/sms/otp/verify"


@Service
class SmsOtpService(
    @Autowired
    val template: RestTemplate
) {
    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Value("\${termii-key}")
    var termiiApiKey: String? = null

    @Value("\${otp-expiry-mins}")
    var otpExpiryMins: Int = 5


    fun sendOtp(phone: String): OtpRefBody? {

        val requestBody = JsonObject().apply {
            addProperty("api_key", termiiApiKey)
            addProperty("from", "N-Alert")
            addProperty("to", phone)
            addProperty("message_type", "NUMERIC")
            addProperty("channel", "dnd")
            addProperty("pin_attempts", 5)
            addProperty("pin_time_to_live", otpExpiryMins)
            addProperty("pin_length", 6)
            addProperty("pin_placeholder", "<otp>")
            addProperty("message_text", "Your Cove login OTP is: <otp>\nIt expires in $otpExpiryMins minutes")
        }

        val headers: HttpHeaders = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }
        val entity = HttpEntity(requestBody.toString(), headers)
        try {

            val response = template
                .postForEntity(SEND_URL, entity, Map::class.java)

            val result = response.body!!
            // Log error when Termii returns any other status
            result["status"].takeIf { it != 200 }?.let {
                throw Exception("SMS OTP provide error code: $it")
            }

            val ref = result["pinId"] as String
            val futureDateTime = Instant.now()
                .plusSeconds(otpExpiryMins.toLong() * 60)
            val expiry = Date.from(futureDateTime)

            return OtpRefBody(ref, phone, expiry, null)
        } catch (e: Exception) {
            logger.error(e.localizedMessage)
            return null
        }

    }

    fun verifyOtp(otp: String, ref: String): String? {
        val requestBody = JsonObject().apply {
            addProperty("api_key", termiiApiKey)
            addProperty("pin_id", ref)
            addProperty("pin", otp)
        }

        val headers: HttpHeaders = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }
        val entity = HttpEntity(requestBody.toString(), headers)
        try {
            val result = template
                .postForEntity(VERIFY_URL, entity, Map::class.java)

            if (result.statusCode != HttpStatus.OK)
                return null

            return result.body!!["msisdn"] as String
        } catch (e: Exception) {
            logger.error(e.localizedMessage)
            return null
        }
    }
}