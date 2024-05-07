package ng.cove.web.data.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import org.springframework.data.mongodb.core.mapping.Field

class JoinRequestId {
    @field:NotBlank
    @Field("community_id")
    @JsonProperty("community_id")
    lateinit var communityId: String

    @field:NotBlank
    lateinit var phone: String
}