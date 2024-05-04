package co.gatedaccess.web.http.controller

import co.gatedaccess.web.data.model.JoinRequestId
import co.gatedaccess.web.data.model.Member
import co.gatedaccess.web.http.body.GuardInputBody
import co.gatedaccess.web.service.CommunityService
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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
    @PostMapping("/guard")
    fun addSecurityGuard(
        @RequestAttribute("user") user: Member,
        @RequestBody @Valid guardData: GuardInputBody,
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

    @DeleteMapping("/guard/{guard_id}")
    fun removeSecurityGuard(
        @RequestAttribute("user") user: Member,
        @PathVariable("guard_id") guardId: String,
    ): ResponseEntity<*> {
        return communityService.removeSecurityGuardFromCommunity(guardId, user.id!!)
    }

//    @PostMapping("{admin_id}")
//    fun addAdmin(
//        @RequestHeader("x-device-id") deviceId: String,
//        @RequestAttribute("user") userId: String,
//        @PathVariable("admin_id") adminIdToAdd: String
//    ): ResponseEntity<*> {
//        println("Admin controller is working")
//        return ResponseEntity.noContent().build<Any>()
//    }
//
//    @DeleteMapping("{admin_id}")
//    fun removeAdmin(
//        @RequestHeader("x-device-id") deviceId: String,
//        @RequestAttribute("user") userId: String,
//        @PathVariable("admin_id") adminIdToDelete: String,
//    ): ResponseEntity<*> {
//        println("Admin controller is working")
//        return ResponseEntity.noContent().build<Any>()
//    }

}