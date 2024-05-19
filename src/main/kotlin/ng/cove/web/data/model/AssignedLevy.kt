package ng.cove.web.data.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.mongodb.lang.NonNull
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.Date

@Document("assigned_levy")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@CompoundIndex(name = "levy_member", def = "{'levy_id': 1, 'member_id': 1}", unique = true)
class AssignedLevy {
    @Id
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var id: String? = null

    @Field("levy_id")
    @field:NonNull
    var levyId: String? = null

    @Field("member_id")
    @field:NonNull
    var memberId: String? = null

    @Field("assigned_by")
    @field:NonNull
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var assignedBy: String? = null

    @FutureOrPresent(message = "Next payment due date cannot be in the past")
    @field:NonNull
    @field:NotNull
    @Field("next_payment_due")
    var nextPaymentDue: Date? = null

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Field("created_at")
    @CreatedDate
    var createdAt: Date? = null
}