package ng.cove.web.http.body

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
class AccessInfoBody {

    @field:Size(min = 1, message = "Head count must be at least 1")
    @field:NotNull
    val headCount = 1

    @field:NotNull
    @field:Future(message = "Valid until must be in the future")
    val validUntil: Date = Date.from(Instant.from(LocalDateTime.now().plusHours(24)))

    @field:JsonProperty("duration_of_visit_in_sec")
    val durationOfVisitInSec: Long? = null
}