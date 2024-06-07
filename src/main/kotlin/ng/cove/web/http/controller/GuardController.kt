package ng.cove.web.http.controller

import com.google.api.client.http.HttpStatusCodes
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import ng.cove.web.data.model.Booking
import ng.cove.web.data.model.SecurityGuard
import ng.cove.web.service.BookingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@ApiResponse(
    description = "Unauthorized",
    responseCode = "401",
    content = [Content(schema = Schema(implementation = String::class),
        mediaType = MediaType.TEXT_PLAIN_VALUE)]
)
@RestController
@RequestMapping("$API_VERSION/guard")
class GuardController {

    @Autowired
    lateinit var bookingService: BookingService

    @ApiResponse(
        description = "Check in successful",
        responseCode = "200",
        content = [Content(schema = Schema(implementation = Booking::class))]
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
        return bookingService.checkInVisitor(code, user)
    }

    @ApiResponse(
        description = "Check out successful",
        responseCode = "200",
        content = [Content(schema = Schema(implementation = Booking::class))]
    )
    @ApiResponse(
        description = "Checked-in booking not found",
        responseCode = "400",
        content = [Content(schema = Schema(implementation = String::class))]
    )
    @Operation(summary = "Check out visitor")
    @GetMapping("/check-out/{code}")
    fun checkOut(
        @RequestAttribute("user") user: SecurityGuard,
        @PathVariable code: String
    )
            : ResponseEntity<*> {
        return bookingService.checkOutVisitor(code, user)
    }
}