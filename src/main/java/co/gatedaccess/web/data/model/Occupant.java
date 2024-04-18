package co.gatedaccess.web.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.lang.NonNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document
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
    @NonNull
    @DBRef(lazy = true)
    Member guardian;
    @Field("photo_url")
    String photoUrl;
    @Field("created_at")
    @JsonProperty("created_at")
    @CreatedDate
    Date createdAt;

    public Occupant(String id, String firstName, String lastName, String phone, String gender, @NonNull Member guardian, String photoUrl, Date createdAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.gender = gender;
        this.guardian = guardian;
        this.photoUrl = photoUrl;
        this.createdAt = createdAt;
    }
}
