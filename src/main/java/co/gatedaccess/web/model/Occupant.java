package co.gatedaccess.web.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;

public class Occupant {
    @Id
    String id;
    @Indexed
    @Field("first_name")
    String firstName;
    @Indexed
    @Field("last_name")
    String lastName;
    String phone;
    String gender;
    @DBRef
    Member guardian;
    @Field("photo_url")
    String photoUrl;
}
