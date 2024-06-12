package ng.cove.web.data.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.mongodb.lang.NonNull
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotNull
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.Date

@Document("issued_bills")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@CompoundIndex(name = "bill_member", def = "{'bill_id': 1, 'member_id': 1}", unique = true)
open class IssuedBill {
    @Id
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var id: String? = null

    @Field("bill_id")
    @field:NonNull
    var billId: String? = null

    @Field("member_id")
    @field:NonNull
    var memberId: String? = null

    @Field("issued_by")
    @field:NonNull
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var issuedBy: String? = null

    @FutureOrPresent(message = "Next payment due date cannot be in the past")
    @field:NotNull
    @Field("next_payment_due")
    var nextPaymentDue: Date? = null

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Field("created_at")
    @CreatedDate
    var createdAt: Date? = null

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Field("last_updated_at")
    @LastModifiedDate
    var lastUpdatedAt: Date? = null
}