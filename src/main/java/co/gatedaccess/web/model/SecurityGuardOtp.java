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
    @CreatedDate
    @Field("created_at")
    Date createdAt;

    public String getId() {
        return id;
    }

    @NonNull
    public String getCode() {
        return code;
    }

    @NonNull
    public String getCommunityId() {
        return communityId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
