package co.gatedaccess.web.service

import co.gatedaccess.web.data.model.Member
import co.gatedaccess.web.data.repo.MemberRepo
import co.gatedaccess.web.http.response.TokenBody
import co.gatedaccess.web.util.CodeGenerator
import co.gatedaccess.web.util.CodeType
import com.google.api.client.auth.openidconnect.IdToken
import com.google.api.client.auth.openidconnect.IdTokenVerifier
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.firebase.auth.FirebaseAuth
import com.mongodb.DuplicateKeyException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService {
    @Autowired
    var memberRepo: MemberRepo? = null

    @Autowired
    private val codeGenerator: CodeGenerator? = null

    @Value("\${google.client.id}")
    var GOOGLE_CLIENT_ID: String? = null

    fun getCustomTokenForClientLogin(token: String?, provider: String): ResponseEntity<*> {
        try {
            val transport: HttpTransport = GoogleNetHttpTransport.newTrustedTransport()
            val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()

            if (provider.equals("google", ignoreCase = true)) {
                val verifier = GoogleIdTokenVerifier.Builder(
                    transport,
                    jsonFactory
                ) // Specify the CLIENT_ID of the app that accesses the backend:
                    .setAudience(listOf(GOOGLE_CLIENT_ID)) // Or, if multiple clients access the backend:
                    //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                    .build()

                val googleIdToken = verifier.verify(token)
                if (googleIdToken != null) {
                    val payload = googleIdToken.payload

                    // Print user identifier
                    val googleUserId = payload.subject

                    // Get profile information from payload
                    val email = payload.email
                    //boolean emailVerified = payload.getEmailVerified();
                    val name = payload["name"] as String?
                    val photoUrl = payload["picture"] as String?
                    //String locale = (String) payload.get("locale");
                    val familyName = payload["family_name"] as String?
                    val givenName = payload["given_name"] as String?

                    val userId: String?
                    if (memberRepo!!.existsMemberByEmail(email)) {
                        userId = memberRepo!!.findMemberById(email).id
                    } else {
                        val member = Member.Builder()
                            .withPhotoUrl(photoUrl)
                            .withFirstName(givenName)
                            .withLastName(familyName)
                            .withGoogleUserId(googleUserId)
                            .withEmailVerifiedAt(Date())
                            .withEmail(email).build()
                        userId = memberRepo!!.save(member).id
                    }
                    val customFirebaseToken = FirebaseAuth.getInstance().createCustomToken(userId)
                    return ResponseEntity.ok(TokenBody(customFirebaseToken))
                } else {
                    return ResponseEntity.badRequest().body("Invalid google token")
                }
            } else {
                val verifier = IdTokenVerifier.Builder()
                    .setIssuer("https://appleid.apple.com")
                    .setAudience(listOf("your_client_id")) // Replace with your client ID
                    .setIssuers(listOf("https://appleid.apple.com"))
                    .build()

                val idToken = IdToken.parse(jsonFactory, token)
                verifier.verify(idToken)

                //TODO: implement Apple Auth
                return ResponseEntity.badRequest()
                    .body(String.format("The %s provider is not supported at this time", provider))
            }
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(e.localizedMessage)
        }
    }

    fun getCommunityInviteCode(userId: String?): ResponseEntity<Any> {
        var member = memberRepo!!.findMemberById(userId)

        if (member != null && member.community != null) {
            while (member.inviteCode == null) {
                //Recursively try to update users invite code if the update fails due to possible duplicate
                try {
                    member.inviteCode = codeGenerator!!.getCode(CodeType.community)
                    member = memberRepo!!.save(member)
                } catch (ignore: DuplicateKeyException) {
                }
            }
            return ResponseEntity.ok(Optional.of(member.inviteCode!!))
        }
        return ResponseEntity.badRequest().build()
    }
}
