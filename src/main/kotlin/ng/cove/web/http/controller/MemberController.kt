package ng.cove.web.http.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import ng.cove.web.data.model.Booking
import ng.cove.web.data.model.JoinRequest
import ng.cove.web.data.model.Member
import ng.cove.web.service.CommunityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@ApiResponse(
    description = "Unauthorized",
    responseCode = "401",
    content = [Content(schema = Schema(implementation = String::class))]
)
@RestController
@RequestMapping("/member")
class MemberController{

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
        content = [Content(schema = Schema(implementation = Booking::class))]
    )
    @ApiResponse(
        description = "User does not have a community",
        responseCode = "500",
        content = [Content(schema = Schema(implementation = String::class))]
    )
    @Operation(summary = "Create access code for visitor")
    @PostMapping("/visitor/book")
    fun getAccessCode(
        @RequestAttribute("user") user: Member,
        @Valid @RequestBody entry: Booking
    )
            : ResponseEntity<*> {
        return communityService.bookVisitor(entry, user)
    }

}