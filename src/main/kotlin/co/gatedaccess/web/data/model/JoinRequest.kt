package co.gatedaccess.web.data.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.mongodb.lang.NonNull
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document("join_request")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
class JoinRequest {

    @Id
    @field:NotNull
    var id: JoinRequestId? = null

    @NonNull
    @Field("referrer_id")
    @JsonIgnore
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
    var createdAt: Date? = null

    @JsonIgnore
    @Field("accepted_at")
    var acceptedAt: Date? = null
}
