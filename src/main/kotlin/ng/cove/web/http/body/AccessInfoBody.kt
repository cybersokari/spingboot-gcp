package ng.cove.web.http.body

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.mongodb.lang.NonNull
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.util.*

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
class AccessInfoBody {

    @field:Min(value = 1, message = "Head count must be at least 1")
    @field:Max(value = 10000, message = "Head count must be at most 10000")
    @field:NotNull
    val headCount = 1

    @field:NotNull
    @field:NonNull
    @field:Future(message = "ValidUntil must be in the future")
    val validUntil: Date? = null

    @field:NotNull
    @field:NonNull
    @field:JsonProperty("duration_of_visit_in_sec")
    val durationOfVisitInSec: Long? = null
}