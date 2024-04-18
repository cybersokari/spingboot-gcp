package co.gatedaccess.web.data.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document
public class Device {
    @Id
    String id;
    String name;
    @Field("fcm_token")
    String fcmToken;
    @Indexed(unique = true)
    @Field("member_id")
    String memberId;
    @CreatedDate
    @Field("created_at")
    Date createdAt;

    public String getId() {
        return id;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
