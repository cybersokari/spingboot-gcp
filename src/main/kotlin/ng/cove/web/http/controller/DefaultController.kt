package ng.cove.web.http.controller

import ng.cove.web.data.model.UserType
import ng.cove.web.http.body.LoginBody
import ng.cove.web.http.body.OtpRefBody
import ng.cove.web.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class DefaultController: BaseController() {

    @Autowired
    lateinit var userService: UserService

    /**
     * Enable the client app to request for OTP with user phone number
     *
     * @param phone user's phone number
     * @return An OTP reference
     */
    @ApiResponse(
        description = "OTP reference",
        responseCode = "200",
        content = [Content(schema = Schema(name = "message", implementation = OtpRefBody::class))]
    )
    @ApiResponse(
        description = "No user found",
        responseCode = "204",
        content = [Content(schema = Schema(name = "message", implementation = String::class))]
    )

    @Operation(summary = "Log in User with phone number")
    @GetMapping("/user/login")
    fun loginUserWithPhone(
        @RequestParam phone: String,
    ): ResponseEntity<*> {
        return userService.getOtpForLogin(phone, UserType.Member)
    }

    @ApiResponse(
        description = "Verification successful",
        responseCode = "200",
        content = [Content(schema = Schema(name = "Custom token", implementation = String::class))]
    )
    @ApiResponse(
        description = "Internal server error",
        responseCode = "500"
    )
    @Operation(summary = "Verify login OTP for user")
    @PostMapping("/user/login/verify")
    fun verifyUserPhoneOtp(
        @RequestBody @Valid body: LoginBody
    ): ResponseEntity<*> {
        return userService.verifyPhoneOtp(body, UserType.Member)
    }

    @ApiResponse(
        description = "Otp reference",
        responseCode = "200",
        content = [Content(schema = Schema(implementation = OtpRefBody::class),
            mediaType = MediaType.APPLICATION_JSON_VALUE)]
    )
    @ApiResponse(
        description = "Invalid OTP",
        responseCode = "404",
        content = [Content(schema = Schema(implementation = String::class))]
    )
    @Operation(summary = "Log in Security guard with phone number")
    @GetMapping("/guard/login")
    fun loginSecurityGuard(
        @RequestParam phone: String,
    ): ResponseEntity<*>? {
        return userService.getOtpForLogin(phone, UserType.Guard)
    }

    @ApiResponse(
        description = "New Firebase JWT",
        responseCode = "200",
        content = [Content(schema = Schema(implementation = String::class))]
    )
    @Operation(summary = "Verify login OTP for Security guard")
    @PostMapping("/guard/login/verify")
    fun verifyGuardPhoneOtp(
        @Valid @RequestBody body: LoginBody
    ): ResponseEntity<*> {
        return userService.verifyPhoneOtp(body, UserType.Guard)
    }

}
