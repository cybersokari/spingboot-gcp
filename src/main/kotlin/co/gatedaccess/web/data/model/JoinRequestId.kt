package co.gatedaccess.web.data.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import org.springframework.data.mongodb.core.mapping.Field

data class JoinRequestId (

    @field:NotBlank
    @Field("community_id")
    @JsonProperty("community_id")
    var communityId: String,

    @field:NotBlank
    var phone: String
)