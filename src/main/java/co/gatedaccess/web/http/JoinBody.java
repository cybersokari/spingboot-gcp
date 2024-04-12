package co.gatedaccess.web.http;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JoinBody {
    @JsonProperty("community_id")
    String communityId;
    @JsonProperty("admin_phone")
    String adminPhone;

    public String getCommunityId() {
        return communityId;
    }

    public String getAdminPhone() {
        return adminPhone;
    }
}
