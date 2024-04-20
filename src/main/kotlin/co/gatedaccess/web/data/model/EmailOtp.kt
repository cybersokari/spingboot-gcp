package co.gatedaccess.web.data.model;

import jakarta.validation.constraints.Future;
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
    @Future
    @Field("expire_at")
    Date expireAt;
}
