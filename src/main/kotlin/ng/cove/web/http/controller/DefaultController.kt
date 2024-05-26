package ng.cove.web.http.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import jakarta.validation.Valid
import ng.cove.web.data.model.UserType
import ng.cove.web.http.ExceptionHandler
import ng.cove.web.http.body.LoginBody
import ng.cove.web.http.body.OtpRefBody
import ng.cove.web.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@SecurityRequirements
class DefaultController{

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
    @GetMapping("/login")
    fun loginWithPhone(
        @RequestParam phone: String,
        @RequestParam type: UserType
    ): ResponseEntity<*> {
        return userService.getOtpForLogin(phone, type)
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
    @PostMapping("/login/verify")
    fun verifyPhoneOtp(
        @RequestBody @Valid body: LoginBody
    ): ResponseEntity<*> {
        return userService.verifyPhoneOtp(body)
    }

}
