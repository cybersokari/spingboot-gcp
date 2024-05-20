package ng.cove.web.component

import com.google.gson.JsonObject
import ng.cove.web.http.body.OtpRefBody
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.*

const val SEND_URL: String = "https://api.ng.termii.com/api/sms/otp/send"
const val VERIFY_URL = "https://api.ng.termii.com/api/sms/otp/verify"


@Service
class SmsOtpService {

    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    // Set in ApplicationStartup
    var termiiApiKey: String? = null

    @Value("\${otp.expiry-mins}")
    var otpExpiryMins: Int = 1

    @Autowired
    lateinit var template : RestTemplate

    fun sendOtp(phone: String): OtpRefBody? {

        val requestBody = JsonObject()
        requestBody.addProperty("api_key", termiiApiKey)
        requestBody.addProperty("from", "N-Alert")
        requestBody.addProperty("to", phone)
        requestBody.addProperty("message_type", "NUMERIC")
        requestBody.addProperty("channel", "dnd")
        requestBody.addProperty("pin_attempts", 5)
        requestBody.addProperty("pin_time_to_live", otpExpiryMins)
        requestBody.addProperty("pin_length", 6)
        requestBody.addProperty("pin_placeholder", "<otp>")
        requestBody.addProperty("message_text", "Your login OTP is: <otp>")


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
            val futureDateTime = Date().toInstant()
                .plusSeconds(otpExpiryMins.toLong() * 60)
            val expiry = Date.from(futureDateTime)

            return OtpRefBody(ref, phone, expiry, null)
        } catch (e: Exception) {
            logger.error(e.localizedMessage)
            return null
        }

    }

    fun verifyOtp(otp: String, ref: String): String? {
        val requestBody = JsonObject()
        requestBody.addProperty("api_key", termiiApiKey)
        requestBody.addProperty("pin_id", ref)
        requestBody.addProperty("pin", otp)


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