package ng.cove.web.http.controller

import ng.cove.web.data.model.JoinRequestId
import ng.cove.web.data.model.Member
import ng.cove.web.http.body.GuardInfoBody
import ng.cove.web.service.CommunityService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import ng.cove.web.data.model.Levy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
@ApiResponse(
    description = "Unauthorized",
    responseCode = "401"
)
@RestController
@RequestMapping("/admin")
class AdminController : BaseController() {

    @Autowired
    lateinit var communityService: CommunityService


    @ApiResponse(
        description = "Request updated",
        responseCode = "200",
        content = [Content(schema = Schema(implementation = String::class))]
    )
    @ApiResponse(
        description = "Request already accepted",
        responseCode = "202",
        content = [Content(schema = Schema(implementation = String::class))]
    )
    @Operation(summary = "Handle community join request")
    @PostMapping("/community/request/{accept}")
    fun handleCommunityJoinRequest(
        @RequestAttribute("user") user: Member,
        @RequestBody @Valid requestId: JoinRequestId,
        @PathVariable accept: Boolean
    ): ResponseEntity<*> {
        return communityService.handleCommunityJoinRequest(user.id!!, requestId, accept)
    }

    /**
     * Add security guard to community
     */
    @ApiResponse(
        description = "Security guard added",
        responseCode = "200",
        content = [Content(schema = Schema(implementation = String::class))]
    )
    @ApiResponse(
        description = "User not Admin or Phone number already used",
        responseCode = "400",
        content = [Content(schema = Schema(implementation = String::class))]
    )
    @Operation(summary = "Add security guard to community")
    @PostMapping("/guard")
    fun addSecurityGuard(
        @RequestAttribute("user") user: Member,
        @RequestBody @Valid guardData: GuardInfoBody,
    ): ResponseEntity<*> {
        return communityService.addSecurityGuardToCommunity(guardData, user.id!!)
    }

    /**
     * Remove Security Guard from a community
     */
    @ApiResponse(
        description = "Security Guard removed",
        responseCode = "200",
        content = [Content(schema = Schema(implementation = String::class))]
    )
    @Operation(summary = "Remove security guard from community")
    @DeleteMapping("/guard/{guard_id}")
    fun removeSecurityGuard(
        @RequestAttribute("user") user: Member,
        @PathVariable("guard_id") guardId: String,
    ): ResponseEntity<*> {
        return communityService.removeSecurityGuardFromCommunity(guardId, user.id!!)
    }

    @ApiResponse(
        description = "Levy created",
        responseCode = "200",
        content = [Content(schema = Schema(implementation = Levy::class),
            mediaType = MediaType.APPLICATION_JSON_VALUE)]
    )
    @ApiResponse(
        description = "Levy name already exists",
        responseCode = "400",
        content = [Content(schema = Schema(implementation = Levy::class),
            mediaType = MediaType.APPLICATION_JSON_VALUE)]
    )
    @Operation(summary = "Create a new levy")
    @PostMapping("/levy")
    fun createLevy(@RequestAttribute("user") user: Member,
                   @RequestBody levy: Levy): ResponseEntity<*> {
        return communityService.createLevy(levy, user)
    }

}