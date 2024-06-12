package ng.cove.web.service

import com.google.gson.JsonObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

/***
 * This class takes a custom token and returns an id token.
 * The id token is used to authenticate the user.
 *
 * While the Client SDKs already handles this, we can use this to speed up
 * the process of testing the API on SwaggerUI
 */
@Service
class IdTokenService(@Autowired
                     val template: RestTemplate
) {
    val firebaseAuthUrl = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithCustomToken?key="

    @Value("\${firebase-client-key}")
    var firebaseApiKey: String? = null

    fun getIdToken(customToken: String): String{
        val requestBody = JsonObject().apply {
            addProperty("token", customToken)
            addProperty("returnSecureToken", true)
        }

        val headers: HttpHeaders = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }
        val entity = HttpEntity(requestBody.toString(), headers)
        val response = template
            .postForEntity("$firebaseAuthUrl${firebaseApiKey!!}", entity, Map::class.java)

        val result = response.body!!

        return result["idToken"] as String
    }
}