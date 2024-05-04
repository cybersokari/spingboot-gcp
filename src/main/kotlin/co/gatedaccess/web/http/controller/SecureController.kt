package co.gatedaccess.web.http.controller

import co.gatedaccess.web.data.model.JoinRequest
import co.gatedaccess.web.data.model.Member
import co.gatedaccess.web.service.CommunityService
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
    @PostMapping("/community/invite")
    fun requestToJoinCommunity(
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
}