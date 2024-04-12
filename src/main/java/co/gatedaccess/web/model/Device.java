package co.gatedaccess.web.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class Device {
    @Id
    String id;
    String name;
    @Field("fcm_token")
    String fcmToken;
}
