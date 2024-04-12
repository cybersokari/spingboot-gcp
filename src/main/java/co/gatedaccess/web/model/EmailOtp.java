package co.gatedaccess.web.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document("email_otp")
public class EmailOtp {
    @Id
    String id;
    String code;
    String email;
    @Field("expire_at")
    Date expireAt;
}
