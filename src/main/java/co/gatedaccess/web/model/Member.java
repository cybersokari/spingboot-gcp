package co.gatedaccess.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.lang.NonNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document
public class Member {
    @Id
    String id;

    @NonNull
    @JsonProperty("first_name")
    @Field("first_name")
    String firstName;

    @NonNull
    @JsonProperty("last_name")
    @Field("last_name")
    String lastName;

    @NonNull
    String gender;

    @NonNull
    @Indexed(unique = true)
    String email;

    @Indexed(unique = true)
    String phone;

    @JsonProperty("phone_verified_at")
    @Field(value = "phone_verified_at")
    Date phoneVerifiedAt;

    @JsonProperty("email_verified_at")
    @Field(value = "email_verified_at")
    Date emailVerifiedAt;

    String address;

    @JsonProperty("photo_url")
    @Field(value = "photo_url")
    String photoUrl;

    @DBRef(lazy = true)
    Community community;

    @DBRef
    Device device;

    @NonNull
    @JsonProperty("invite_code")
    @Indexed(unique = true)
    @Field("invite_code")
    String inviteCode;

    @JsonProperty("created_at")
    @Field("created_at")
    @CreatedDate
    Date createdAt;

    @JsonProperty("last_modified_at")
    @Field("last_modified_at")
    @LastModifiedDate
    Date lastModifiedAt;

    @Indexed(unique = true, sparse = true)
    @JsonProperty("google_id")
    @Field("google_id")
    String googleUserId;

    @JsonProperty("apple_id")
    @Indexed(unique = true, sparse = true)
    @Field("apple_id")
    String appleUserId;

    public Member(String id, @NonNull String firstName, @NonNull String lastName, @NonNull String gender, @NonNull String email, String phone, Date phoneVerifiedAt, Date emailVerifiedAt, String address, String photoUrl, Community community, Device device, @NonNull String inviteCode, Date createdAt, Date lastModifiedAt, String googleUserId, String appleUserId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.phoneVerifiedAt = phoneVerifiedAt;
        this.emailVerifiedAt = emailVerifiedAt;
        this.address = address;
        this.photoUrl = photoUrl;
        this.community = community;
        this.device = device;
        this.inviteCode = inviteCode;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
        this.googleUserId = googleUserId;
        this.appleUserId = appleUserId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getPhoneVerifiedAt() {
        return phoneVerifiedAt;
    }

    public void setPhoneVerifiedAt(Date phoneVerifiedAt) {
        this.phoneVerifiedAt = phoneVerifiedAt;
    }

    public Date getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    public void setEmailVerifiedAt(Date emailVerifiedAt) {
        this.emailVerifiedAt = emailVerifiedAt;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public static final class Builder {
        private String id;
        private String firstName;
        private String lastName;
        private String gender;
        private String email;
        private String phone;
        private Date phoneVerifiedAt;
        private Date emailVerifiedAt;
        private String address;
        private String photoUrl;
        private Community community;
        private Device device;
        private String inviteCode;
        private Date createdAt;
        private Date lastModifiedAt;
        private String googleUserId;
        private String appleUserId;

        public Builder() {
        }

        public static Builder aMember() {
            return new Builder();
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder withGender(String gender) {
            this.gender = gender;
            return this;
        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder withPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder withPhoneVerifiedAt(Date phoneVerifiedAt) {
            this.phoneVerifiedAt = phoneVerifiedAt;
            return this;
        }

        public Builder withEmailVerifiedAt(Date emailVerifiedAt) {
            this.emailVerifiedAt = emailVerifiedAt;
            return this;
        }

        public Builder withAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder withPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
            return this;
        }

        public Builder withCommunity(Community community) {
            this.community = community;
            return this;
        }

        public Builder withDevice(Device device) {
            this.device = device;
            return this;
        }

        public Builder withInviteCode(String inviteCode) {
            this.inviteCode = inviteCode;
            return this;
        }

        public Builder withCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder withLastModifiedAt(Date lastModifiedAt) {
            this.lastModifiedAt = lastModifiedAt;
            return this;
        }

        public Builder withGoogleUserId(String googleUserId) {
            this.googleUserId = googleUserId;
            return this;
        }

        public Builder withAppleUserId(String appleUserId) {
            this.appleUserId = appleUserId;
            return this;
        }

        public Member build() {
            return new Member(id, firstName, lastName, gender, email, phone, phoneVerifiedAt, emailVerifiedAt, address, photoUrl, community, device, inviteCode, createdAt, lastModifiedAt, googleUserId, appleUserId);
        }
    }
}
