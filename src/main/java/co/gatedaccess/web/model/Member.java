package co.gatedaccess.web.model;

import com.mongodb.lang.NonNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Date;

@Document
public class Member {
    @Id
    String id;
    @NonNull
    @Field("first_name")
    String firstName;
    @NonNull
    @Field("last_name")
    String lastName;
    @NonNull
    String gender;
    @NonNull
    @Indexed(unique = true)
    String email;
    String phone;
    @Field(value = "phone_verified_at")
    Date phoneVerifiedAt;
    @Field(value = "email_verified_at")
    Date emailVerifiedAt;
    String address;
    @Field(value = "photo_url")
    String photoUrl;
    @DBRef(lazy = true)
    Community community;
    @DBRef
    Device device;
    @NonNull
    @Indexed(unique = true)
    @Field("invite_code")
    String inviteCode;
    @Field("created_at")
    @CreatedDate
    Date createdAt;
    @Field("last_modified_at")
    @LastModifiedDate
    Date lastModifiedAt;

    public Member(String id, @NonNull String firstName, @NonNull String lastName, @NonNull String gender, @NonNull String email, String phone, Date phoneVerifiedAt, Date emailVerifiedAt, String address, String photoUrl, Community community, Device device, @NonNull String inviteCode, Date createdAt) {
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
}
