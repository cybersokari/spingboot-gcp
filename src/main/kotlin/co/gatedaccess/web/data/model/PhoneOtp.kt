package co.gatedaccess.web.data.model;

import jakarta.validation.constraints.Future;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document("phone_otp")
public class PhoneOtp {
    @Id
    String id;
    String code;
    String phone;
    @Future
    @Field("expire_at")
    Date expireAt;
    @CreatedDate
    @Field("created_at")
    Date createdAt;
}
