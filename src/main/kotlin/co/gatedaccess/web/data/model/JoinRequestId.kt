package co.gatedaccess.web.data.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.mongodb.lang.NonNull
import jakarta.validation.constraints.NotBlank
import org.springframework.data.mongodb.core.mapping.Field

class JoinRequestId {
    @NotBlank
    @NonNull
    @Field("community_id")
    @JsonProperty("community_id")
    lateinit var communityId: String

    @NotBlank
    @NonNull
    lateinit var phone: String
}