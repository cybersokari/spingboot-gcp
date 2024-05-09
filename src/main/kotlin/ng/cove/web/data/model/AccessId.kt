package ng.cove.web.data.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.mongodb.core.mapping.Field

class AccessId {
    constructor(code: String, communityId: String) {
        this.code = code
        this.communityId = communityId
    }

    @Field("code")
    @JsonProperty("code")
    var code: String
    @Field("community_id")
    @JsonProperty("community_id")
    var communityId: String
}