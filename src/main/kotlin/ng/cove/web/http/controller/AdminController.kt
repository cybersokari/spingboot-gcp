package ng.cove.web.http.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import ng.cove.web.data.model.*
import ng.cove.web.http.ExceptionHandler
import ng.cove.web.http.body.GuardInfoBody
import ng.cove.web.http.body.IssuableBillBody
import ng.cove.web.service.BillService
import ng.cove.web.service.CommunityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@ApiResponse(
    description = "Unauthorized",
    responseCode = "401",
    content = [Content(schema = Schema(implementation = String::class))]
)
@RestController
@RequestMapping("/admin")
class AdminController{

    @Autowired
    lateinit var communityService: CommunityService
    @Autowired
    lateinit var billService: BillService

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
    @Operation(summary = "Accept or reject join request")
    @PostMapping("/community/request/{id}")
    fun handleCommunityJoinRequest(
        @RequestAttribute("user") user: Admin,
        @PathVariable("id") requestId: String,
        @RequestParam accept: Boolean
    ): ResponseEntity<*> {
        return communityService.handleCommunityJoinRequest(user, requestId, accept)
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
        @RequestAttribute("user") user: Admin,
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
        @RequestAttribute("user") user: Admin,
        @PathVariable("guard_id") guardId: String,
    ): ResponseEntity<*> {
        return communityService.removeSecurityGuardFromCommunity(guardId, user.id!!)
    }

    @ApiResponse(
        description = "Bill created",
        responseCode = "200",
        content = [Content(
            schema = Schema(implementation = Bill::class),
            mediaType = MediaType.APPLICATION_JSON_VALUE
        )]
    )
    @ApiResponse(
        description = "Bill name already exists",
        responseCode = "400",
        content = [Content(
            schema = Schema(implementation = Bill::class),
            mediaType = MediaType.APPLICATION_JSON_VALUE
        )]
    )
    @Operation(summary = "Create a new Bill")
    @PostMapping("/bill")
    fun createBill(
        @RequestAttribute("user") user: Admin,
        @RequestBody bill: Bill
    ): ResponseEntity<*> {
        return billService.createBill(bill, user)
    }

    @ApiResponse(
        description = "Bill issued",
        responseCode = "200",
        content = [Content(
            schema = Schema(implementation = IssuedBill::class),
            mediaType = MediaType.APPLICATION_JSON_VALUE
        )]
    )
    @ApiResponse(
        description = "Bill already issued",
        responseCode = "400",
        content = [Content(
            schema = Schema(implementation = Map::class),
            mediaType = MediaType.APPLICATION_JSON_VALUE
        )]
    )
    @Operation(summary = "Issue a new Bill")
    @PostMapping("/bill/issue", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun issueBill(
        @RequestAttribute("user") user: Admin,
        @Valid @RequestBody bill: IssuableBillBody
    ): ResponseEntity<*> {
        return billService.issueBill(bill, user)
    }


}