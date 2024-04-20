package co.gatedaccess.web.http.controller

import co.gatedaccess.web.http.response.GuardOtpBody
import co.gatedaccess.web.http.response.TokenBody
import co.gatedaccess.web.service.CommunityService
import co.gatedaccess.web.service.UserService
import com.mongodb.lang.NonNull
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.annotation.*
import java.util.*

//@RequestMapping("/v1") Dont use it, it will break the '/secure' path interceptor
@RestController
class RouteController {
    @Autowired
    private val communityService: CommunityService? = null

    @Autowired
    private val userService: UserService? = null


    /**
     * Enable the client app to exchange Google/Apple provider token for a
     * custom Firebase token that can be used to authenticate the client app
     *
     * @param token
     * @param provider
     * @return A custom Firebase token
     */
    @ApiResponse(
        description = "Get custom Firebase JWT",
        responseCode = "200 success",
        content = [Content(schema = Schema(implementation = TokenBody::class))]
    )
    @GetMapping("/user/{provider}/login")
    fun loginUserWithProvider(
        @RequestParam token: String?,
        @PathVariable("provider") provider: String
    ): ResponseEntity<*>? {
        return userService!!.getCustomTokenForClientLogin(token, provider)
    }

    @GetMapping("/secure/community/invite-code")
    fun getCommunityInviteCode(@RequestAttribute("user") userId: String?): ResponseEntity<Any> {
        return userService!!.getCommunityInviteCode(userId)
    }


    @GetMapping("/secure/community/join")
    fun requestToJoinCommunity(
        @RequestAttribute("user") userId: String?,
        @RequestParam("invite-code") inviteCode: String?
    ): ResponseEntity<String?>? {
        return try {
            communityService!!.joinWithInviteCode(inviteCode, userId)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.localizedMessage)
        }
    }

    @GetMapping("/secure/community/request/{request-id}")
    fun handleCommunityJoinRequest(
        @RequestAttribute("user") userId: String?,
        @PathVariable("request-id") requestId: String?,
        @RequestParam accept: Boolean
    ): ResponseEntity<String?>? {
        return communityService!!.handleCommunityJoinRequest(userId, requestId, accept)
    }

    @ApiResponse(
        description = "New OTP",
        responseCode = "200",
        content = [Content(schema = Schema(implementation = GuardOtpBody::class))]
    )
    @ApiResponse(
        description = "User is not an Admin",
        responseCode = "400",
        content = [Content(schema = Schema(implementation = String::class))]
    )
    @GetMapping("/secure/guard-otp/create")
    fun getSecurityGuardOtpForAdmin(@RequestAttribute("user") adminUserId: String?): ResponseEntity<*>? {
        return communityService!!.getSecurityGuardOtpForAdmin(adminUserId)
    }

    @ApiResponse(
        description = "New Firebase JWT",
        responseCode = "200",
        content = [Content(schema = Schema(implementation = TokenBody::class))]
    )
    @ApiResponse(
        description = "Invalid OTP",
        responseCode = "404",
        content = [Content(schema = Schema(implementation = String::class))]
    )
    @GetMapping("/guard-login/{otp}")
    fun loginSecurityGuard(
        @PathVariable otp: String?,
        @RequestHeader("x-device-name") device: String?
    ): ResponseEntity<*>? {
        return communityService!!.getCustomTokenForSecurityGuard(otp, device)
    }

    // Exception handler for MissingRequestHeaderException
    @ExceptionHandler(MissingRequestHeaderException::class)
    fun handleMissingHeader(@NonNull ex: MissingRequestHeaderException): ResponseEntity<String> {
        return ResponseEntity.badRequest().body("Required header '" + ex.headerName + "' is missing.")
    }

    // Exception handler for MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(@NonNull ex: MethodArgumentNotValidException): ResponseEntity<String> {
        return ResponseEntity.badRequest().body("Required argument '" + ex.parameter.parameterName + "' is missing.")
    }
}
