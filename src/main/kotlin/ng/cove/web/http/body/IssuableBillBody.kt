package ng.cove.web.http.body

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.*
import kotlin.collections.Set

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
class IssuableBillBody {

    @field:NotBlank
    var billId: String? = null

    @field:NotBlank
    var members: Set<String>? = null

    @FutureOrPresent(message = "Next payment due date cannot be in the past")
    @field:NotNull
    var nextPaymentDue: Date? = null

}