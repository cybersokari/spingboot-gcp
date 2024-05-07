package ng.cove.web.http.controller

import ng.cove.web.data.model.Access
import ng.cove.web.data.model.JoinRequest
import ng.cove.web.data.model.Member
import ng.cove.web.data.model.SecurityGuard
import ng.cove.web.http.body.AccessInfoBody
import ng.cove.web.service.CommunityService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/secure")
class SecureController : BaseController() {

    @Autowired
    lateinit var communityService: CommunityService

    @ApiResponse(
        description = "Request already sent",
        responseCode = "208",
        content = [Content(schema = Schema(implementation = String::class))]
    )
    @ApiResponse(
        description = "Request sent",
        responseCode = "200",
        content = [Content(schema = Schema(implementation = String::class))]
    )
    @PostMapping("/invite")
    @Operation(summary = "Invite user to community")
    fun inviteUserToCommunity(
        @RequestAttribute("user") user: Member,
        @Valid @RequestBody request: JoinRequest
    ): ResponseEntity<*> {
        return try {
            communityService.inviteUserToCommunity(request, user)
        } catch (e: Exception) {
            ResponseEntity.internalServerError()
                .body(e.localizedMessage)
        }
    }

    @ApiResponse(
        description = "Success",
        responseCode = "200",
        content = [Content(schema = Schema(implementation = Access::class))]
    )
    @ApiResponse(
        description = "User does not have a community",
        responseCode = "500",
        content = [Content(schema = Schema(implementation = String::class))]
    )
    @Operation(summary = "Create access code for visitor")
    @PostMapping("/access")
    fun getAccessCode(
        @RequestAttribute("user") user: Member,
        @Valid @RequestBody info: AccessInfoBody
    )
            : ResponseEntity<*> {
        return communityService.getAccessCodeForVisitor(info, user)
    }

    @ApiResponse(
        description = "Check in successful",
        responseCode = "200",
        content = [Content(schema = Schema(implementation = Access::class))]
    )
    @ApiResponse(
        description = "Access code not found",
        responseCode = "204",
        content = [Content(schema = Schema(implementation = String::class))]
    )
    @Operation(summary = "Check in visitor")
    @GetMapping("/check-in/{code}")
    fun checkIn(
        @RequestAttribute("user") user: SecurityGuard,
        @PathVariable code: String
    )
            : ResponseEntity<*> {
        return communityService.checkInVisitor(code, user)
    }
}