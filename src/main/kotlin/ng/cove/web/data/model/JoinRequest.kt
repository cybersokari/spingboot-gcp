package ng.cove.web.data.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.mongodb.lang.NonNull
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document("join_requests")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@CompoundIndex(name = "community_id_phone", def = "{'community_id': 1, 'phone': 1}", unique = true)
class JoinRequest {

    @Id
    @field:NotNull
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var id: String? = null

    @field:NotBlank @Field("community_id")
    lateinit var communityId: String

    @field:NotBlank
    lateinit var phone: String

    @NonNull
    @Field("referrer_id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var referrerId: String? = null

    @field:NonNull
    @Field("first_name")
    @field:NotBlank(message = "First name cannot be a null")
    var firstName: String? = null

    @NonNull
    @Field("last_name")
    @field:NotBlank(message = "Last name cannot be a null")
    var lastName: String? = null

    @field:NotNull
    @field:NonNull
    var gender: Gender? = null

    var address: String? = null

    @Field("created_at")
    @CreatedDate
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var createdAt: Date? = null

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Field("accepted_at")
    var acceptedAt: Date? = null

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Field("approved_by")
    var approvedBy: String? = null
}
