package co.gatedaccess.web.http;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JoinBody {
    @JsonProperty("community_id")
    String communityId;
    @JsonProperty("invite_code")
    String inviteCode;

    public String getCommunityId() {
        return communityId;
    }

    public String getInviteCode() {
        return inviteCode;
    }
}
