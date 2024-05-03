package co.gatedaccess.web.component

import co.gatedaccess.web.http.body.OtpRefBody
import com.google.gson.JsonObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import java.time.ZoneId
import java.util.*


@Service
class SmsOtpService {
    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    // Set in ApplicationStartup
    var termiiApiKey: String? = null

    fun sendOtp(phone: String): OtpRefBody? {

        val requestBody = JsonObject()
        requestBody.addProperty("api_key", termiiApiKey)
        requestBody.addProperty("from", "N-Alert")
        requestBody.addProperty("to", phone)
        requestBody.addProperty("message_type", "NUMERIC")
        requestBody.addProperty("channel", "dnd")
        requestBody.addProperty("pin_attempts", 5)
        val expiryInMinutes = 10
        requestBody.addProperty("pin_time_to_live", expiryInMinutes)
        requestBody.addProperty("pin_length", 6)
        requestBody.addProperty("pin_placeholder", "<otp>")
        requestBody.addProperty("message_text", "Your login OTP is: <otp>")

        val url = "https://api.ng.termii.com/api/sms/otp/send"
        val client = RestClient.builder().baseUrl(url).defaultHeaders { h ->
            run {
                h.contentType = MediaType.APPLICATION_JSON
            }
        }.build()

        try {

            val result = client.post()
                .body(requestBody.toString())
                .retrieve().toEntity(Map::class.java).body!!

            // Log error when Termii returns any other status
            result["status"].takeIf { it != 200 }?.let {
                throw Exception("SMS OTP provide error code: $it")
            }

            val ref = result["pinId"] as String
            val futureDateTime = Date().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .plusMinutes(expiryInMinutes.toLong())
            val expiry = Date.from(futureDateTime.atZone(ZoneId.systemDefault()).toInstant())

            return OtpRefBody(ref, phone, expiry)
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

        val url = "https://api.ng.termii.com/api/sms/otp/verify"
        val client = RestClient.builder().baseUrl(url).defaultHeaders { h ->
            run {
                h.contentType = MediaType.APPLICATION_JSON
            }
        }.build()
        try {
            val result = client.post().body(requestBody.toString())
                .retrieve().toEntity(Map::class.java)

            if (result.statusCode != HttpStatus.OK)
                return null

            return result.body!!["msisdn"] as String
        } catch (e: Exception) {
            logger.error(e.localizedMessage)
            return null
        }
    }
}