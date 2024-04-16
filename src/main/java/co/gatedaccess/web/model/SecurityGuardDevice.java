package co.gatedaccess.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.lang.NonNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;

@Document("security_guard_device")
public class SecurityGuardDevice {
    @MongoId
    String id;
    @JsonProperty("device_name")
    @Field("device_name")
    String deviceName;
    @NonNull
    @Indexed
    @Field("community_id")
    @JsonProperty("community_id")
    String communityId;
    @CreatedDate
    @Field("created_at")
    Date createdAt;

    public String getId() {
        return id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    @NonNull
    public String getCommunityId() {
        return communityId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public static final class Builder {
        private String deviceName;
        private String communityId;

        public Builder() {
        }

        public static Builder aSecurityGuardDevice() {
            return new Builder();
        }

        public Builder withDeviceName(String deviceName) {
            this.deviceName = deviceName;
            return this;
        }

        public Builder withCommunityId(String communityId) {
            this.communityId = communityId;
            return this;
        }

        public SecurityGuardDevice build() {
            SecurityGuardDevice securityGuardDevice = new SecurityGuardDevice();
            securityGuardDevice.communityId = this.communityId;
            securityGuardDevice.deviceName = this.deviceName;
            return securityGuardDevice;
        }
    }
}
