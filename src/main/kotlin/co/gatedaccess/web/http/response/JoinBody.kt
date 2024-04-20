package co.gatedaccess.web.http.response

import com.fasterxml.jackson.annotation.JsonProperty

class JoinBody {
    @JsonProperty("community_id")
    var communityId: String? = null

    @JsonProperty("invite_code")
    var inviteCode: String? = null
}
