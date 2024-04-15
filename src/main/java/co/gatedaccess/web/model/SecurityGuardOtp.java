package co.gatedaccess.web.model;

import com.mongodb.lang.NonNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;

@Document("security_guard_otp")
public class SecurityGuardOtp {
    @MongoId
    String id;

    @NonNull
    @Indexed(unique = true)
    String code;

    @NonNull
    @Indexed(unique = true)
    String communityId;

    @NonNull
    @Field("expire_at")
    Date expireAt;

    public SecurityGuardOtp() {
    }

    @CreatedDate
    @Field("created_at")
    Date createdAt;

    public SecurityGuardOtp(@NonNull String communityId, @NonNull Date expireAt, @NonNull String code) {
        this.communityId = communityId;
        this.expireAt = expireAt;
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getCommunityId() {
        return communityId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    @NonNull
    public Date getExpireAt() {
        return expireAt;
    }

    public void setCode(@NonNull String code) {
        this.code = code;
    }

    public void setCommunityId(@NonNull String communityId) {
        this.communityId = communityId;
    }

    public void setExpireAt(@NonNull Date expireAt) {
        this.expireAt = expireAt;
    }

    public static final class Builder {
        private Date expireAt;
        private String communityId;
        private String code;

        public Builder() {
        }

        public static Builder aSecurityGuardOtp() {
            return new Builder();
        }

        public Builder withExpireAt(Date expireAt) {
            this.expireAt = expireAt;
            return this;
        }

        public Builder withCommunityId(String communityId) {
            this.communityId = communityId;
            return this;
        }

        public Builder withCode(String code) {
            this.code = code;
            return this;
        }

        public SecurityGuardOtp build() {
            SecurityGuardOtp securityGuardOtp = new SecurityGuardOtp();
            securityGuardOtp.communityId = this.communityId;
            securityGuardOtp.expireAt = this.expireAt;
            securityGuardOtp.code = this.code;
            return securityGuardOtp;
        }
    }
}
