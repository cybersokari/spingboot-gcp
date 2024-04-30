package co.gatedaccess.web.service

import co.gatedaccess.web.data.model.UserType
import co.gatedaccess.web.data.repo.MemberRepo
import co.gatedaccess.web.data.repo.SecurityGuardRepo
import co.gatedaccess.web.http.body.LoginBody
import co.gatedaccess.web.http.body.OtpRefBody
import co.gatedaccess.web.util.CodeGenerator
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.JsonObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import java.time.ZoneId
import java.util.*


@Service
class UserService {
    @Autowired
    private lateinit var restTemplateBuilder: RestTemplateBuilder
    val logger: Logger = LoggerFactory.getLogger(this::class.java)
    @Autowired
    lateinit var memberRepo: MemberRepo

    @Autowired
    lateinit var guardRepo: SecurityGuardRepo

    @Autowired
    private val codeGenerator: CodeGenerator? = null

    @Value("\${google.client.id}")
    var googleClientId: String? = null

    @Value("\${termii.api.key}")
    var termiiApiKey: String? = null


    fun getOtpForLogin(phone: String, userType: UserType): ResponseEntity<*> {

        if(userType == UserType.Member){
            memberRepo.findByPhoneAndCommunityIsNotNull(phone)
                ?: return ResponseEntity.badRequest().body("$phone is not a member of a community")
        }else{
            guardRepo.findByPhoneAndCommunityIdIsNotNull(phone)
                ?: return ResponseEntity.badRequest().body("$phone is not guard of a community")
        }

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
            result["status"].takeIf { it != 200 }.let {
                logger.error("termii API status: $it")
            }

            val ref = result["pinId"] as String
            val futureDateTime = Date().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .plusMinutes(expiryInMinutes.toLong())
            val expiry = Date.from(futureDateTime.atZone(ZoneId.systemDefault()).toInstant())
//                val memberPhoneOtp = MemberPhoneOtp(ref,phone,expiry)
//                memberPhoneOtpRepo.save(memberPhoneOtp)

            return ResponseEntity.ok().body(OtpRefBody(ref, phone, expiry))
        } catch (e: Exception) {
            logger.error(e.localizedMessage)
            return ResponseEntity.internalServerError().body(e.localizedMessage)
        }
    }


    /**
     * Verify phone OTP for all user types
     */
    fun verifyPhoneOtp(login: LoginBody, userType: UserType) : ResponseEntity<*>{
        val requestBody = JsonObject()
        requestBody.addProperty("api_key", termiiApiKey)
        requestBody.addProperty("pin_id", login.ref)
        requestBody.addProperty("pin", login.otp)

        val url = "https://api.ng.termii.com/api/sms/otp/verify"
        val client = RestClient.builder().baseUrl(url).defaultHeaders { h ->
            run {
                h.contentType = MediaType.APPLICATION_JSON
            }
        }.build()
        try {
            val result = client.post().body(requestBody.toString())
                .retrieve().toEntity(Map::class.java)

            if(result.statusCode != HttpStatus.OK)
                return ResponseEntity.badRequest().body("Code already confirmed")

            val phone = result.body!!["msisdn"] as String

            val userId : String
            val previousDeviceId: String?
            var userTypeForClaims = userType

            if(userType == UserType.Member) {
                val member = memberRepo.findByPhone(phone)!!
                if(member.phoneVerifiedAt == null){
                    member.phoneVerifiedAt = Date()

                }

                // Grab previousDeviceId before updating Db with new
                previousDeviceId = member.deviceId
                // Update device info

                member.deviceId = login.deviceId
                member.deviceName = login.deviceName
                member.lastLoginAt = Date()
                memberRepo.save(member)

                userId = member.id!!
                // Switch admin user type for JWT claims
                if(member.community!!.adminIds!!.contains(userId)){
                    userTypeForClaims = UserType.Admin
                }

            } else{
                val guard = guardRepo.findByPhone(phone)!!
                if (guard.phoneVerifiedAt == null){
                    guard.phoneVerifiedAt = Date()
                }

                userId = guard.id!!

                // Grab previousDeviceId before updating Db with new
                previousDeviceId = guard.deviceId

                // Update device info
                guard.deviceId = login.deviceId
                guard.deviceName = login.deviceName
                guard.lastLoginAt = Date()
                guardRepo.save(guard)
            }


            val firebaseAuth = FirebaseAuth.getInstance()

            //Revoke refresh token for previous devices
            if(previousDeviceId != null){
                firebaseAuth.revokeRefreshTokens(userId)
            }

            // Set Admin claim for JWT
            val claims = mapOf("type" to userTypeForClaims.name)
            val customToken = firebaseAuth.createCustomToken(userId, claims)

            return ResponseEntity.ok().body(customToken)
        } catch (e: Exception ) {
            logger.error(e.localizedMessage)
            return ResponseEntity.internalServerError().body(e.localizedMessage)
        }
    }

//    fun getCustomTokenForClientLogin(token: String?, provider: String): ResponseEntity<*> {
//        try {
//            val transport: HttpTransport = GoogleNetHttpTransport.newTrustedTransport()
//            val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()
//
//            if (provider.equals("google", ignoreCase = true)) {
//                val verifier = GoogleIdTokenVerifier.Builder(
//                    transport,
//                    jsonFactory
//                ) // Specify the CLIENT_ID of the app that accesses the backend:
//                    .setAudience(listOf(googleClientId)) // Or, if multiple clients access the backend:
//                    .build()
//
//                val googleIdToken = verifier.verify(token)
//                if (googleIdToken != null) {
//                    val payload = googleIdToken.payload
//
//                    // Print user identifier
//                    val googleUserId = payload.subject
//
//                    // Get profile information from payload
//                    val email = payload.email
//                    //boolean emailVerified = payload.getEmailVerified();
//                    val name = payload["name"] as String?
//                    val photoUrl = payload["picture"] as String?
//                    //String locale = (String) payload.get("locale");
//                    val familyName = payload["family_name"] as String?
//                    val givenName = payload["given_name"] as String?
//
//                    val userId: String?
//                    if (memberRepo!!.existsMemberByEmail(email)) {
//                        userId = memberRepo!!.findMemberById(email).id
//                    } else {
//                        val member = Member.Builder()
//                            .withPhotoUrl(photoUrl)
//                            .withFirstName(givenName)
//                            .withLastName(familyName)
//                            .withGoogleUserId(googleUserId)
//                            .withEmailVerifiedAt(Date())
//                            .withEmail(email).build()
//                        userId = memberRepo!!.save(member).id
//                    }
//                    val customFirebaseToken = FirebaseAuth.getInstance().createCustomToken(userId)
//                    return ResponseEntity.ok(TokenBody(customFirebaseToken))
//                } else {
//                    return ResponseEntity.badRequest().body("Invalid google token")
//                }
//            } else {
//                val verifier = IdTokenVerifier.Builder()
//                    .setIssuer("https://appleid.apple.com")
//                    .setAudience(listOf("your_client_id")) // Replace with your client ID
//                    .setIssuers(listOf("https://appleid.apple.com"))
//                    .build()
//
//                val idToken = IdToken.parse(jsonFactory, token)
//                verifier.verify(idToken)
//
//                //TODO: implement Apple Auth
//                return ResponseEntity.badRequest()
//                    .body(String.format("The %s provider is not supported at this time", provider))
//            }
//        } catch (e: Exception) {
//            return ResponseEntity.badRequest().body(e.localizedMessage)
//        }
//    }

//    fun getCommunityInviteCode(userId: String): ResponseEntity<Any> {
//        var member = memberRepo?.findMemberById(userId)
//
//        member?.let {
//            while (it.inviteCode == null) {
//                //Recursively try to update users invite code if the update fails due to possible duplicate
//                try {
//                    it.inviteCode = codeGenerator!!.getCode(CodeType.Community)
//                    member = memberRepo!!.save(it)
//                } catch (ignore: DuplicateKeyException) {
//                }
//            }
//            return ResponseEntity.ok(Optional.of(it.inviteCode!!))
//        }
//
//        return ResponseEntity.badRequest().body("no user with this id: $userId")
//    }
}
