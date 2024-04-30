package co.gatedaccess.web.service

import co.gatedaccess.web.component.SmsOtpComponent
import co.gatedaccess.web.data.model.UserType
import co.gatedaccess.web.data.repo.MemberRepo
import co.gatedaccess.web.data.repo.SecurityGuardRepo
import co.gatedaccess.web.http.body.LoginBody
import co.gatedaccess.web.util.CodeGenerator
import com.google.firebase.auth.FirebaseAuth
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*


@Service
class UserService {

    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var memberRepo: MemberRepo

    @Autowired
    lateinit var guardRepo: SecurityGuardRepo

    @Autowired
    private val codeGenerator: CodeGenerator? = null

    @Autowired
    lateinit var smsOtp: SmsOtpComponent


    fun getOtpForLogin(phone: String, userType: UserType): ResponseEntity<*> {

        if (userType == UserType.Member) {
            memberRepo.findByPhoneAndCommunityIsNotNull(phone)
                ?: return ResponseEntity.badRequest().body("$phone is not a member of a community")
        } else {
            guardRepo.findByPhoneAndCommunityIdIsNotNull(phone)
                ?: return ResponseEntity.badRequest().body("$phone is not guard of a community")
        }

        val otpResult = smsOtp.sendOtp(phone)
        return if (otpResult != null) {
            ResponseEntity.ok().body(otpResult)
        } else {
            ResponseEntity.internalServerError().body("OTP provider error")
        }

    }


    /**
     * Verify phone OTP for all user types
     */
    fun verifyPhoneOtp(login: LoginBody, userType: UserType): ResponseEntity<*> {

        try {
            val phone = smsOtp.verifyOtp(login.otp, login.ref)
                ?: return ResponseEntity.badRequest().body("Invalid code")

            val userId: String
            val previousDeviceId: String?
            var userTypeForClaims = userType

            if (userType == UserType.Member) {
                val member = memberRepo.findByPhone(phone)!!
                //TODO: Redesign this if possible: Its only going to run on first user login, but check forever
                if (member.phoneVerifiedAt == null) {
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
                if (member.community!!.adminIds!!.contains(userId)) {
                    userTypeForClaims = UserType.Admin
                }

            } else {
                val guard = guardRepo.findByPhone(phone)!!
                //TODO: Redesign this if possible: Its only going to run on first user login, but check forever
                if (guard.phoneVerifiedAt == null) {
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
            if (previousDeviceId != null) {
                firebaseAuth.revokeRefreshTokens(userId)
            }

            // Set Admin claim for JWT
            val claims = mapOf("type" to userTypeForClaims.name)
            val customToken = firebaseAuth.createCustomToken(userId, claims)

            return ResponseEntity.ok().body(customToken)
        } catch (e: Exception) {
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
