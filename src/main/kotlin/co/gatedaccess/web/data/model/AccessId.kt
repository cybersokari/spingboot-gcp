package co.gatedaccess.web.data.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.mongodb.core.mapping.Field

class AccessId {
    constructor(code: String, communityId: String) {
        this.code = code
        this.communityId = communityId
    }

    @Field("code")
    @JsonProperty("code")
    lateinit var code: String
    @Field("community_id")
    @JsonProperty("community_id")
    lateinit var communityId: String
}