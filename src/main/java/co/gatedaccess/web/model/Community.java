package co.gatedaccess.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document
public class Community {
    @Id
    private String id;
    @Size(max = 255)
    private String name;
    @Size(max = 255)
    private String address;
    @Size(max = 255)
    private String country;
    @Size(max = 255)
    private String state;
    @Size(max = 255)
    private String desc;
    @DBRef(lazy = true)
    @Field("super_admin")
    @JsonProperty("super_admin")
    private Member superAdmin;
    @Field("created_at")
    @JsonProperty("created_at")
    @CreatedDate
    private Date createdAt;
    @Field("banner_url")
    @JsonProperty("banner_url")
    private String bannerUrl;
    @Field("last_modified_at")
    @LastModifiedDate
    Date lastModifiedAt;

    public Community() {
    }

    public Community(String id, String name, String address, String country, String state, String desc, Member superAdmin, Date createdAt, String bannerUrl) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.country = country;
        this.state = state;
        this.desc = desc;
        this.superAdmin = superAdmin;
        this.createdAt = createdAt;
        this.bannerUrl = bannerUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Member getSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(Member superAdmin) {
        this.superAdmin = superAdmin;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    @Override
    public String toString() {
        return "Community{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", country='" + country + '\'' +
                ", state='" + state + '\'' +
                ", desc='" + desc + '\'' +
                ", superAdmin=" + superAdmin +
                ", createdAt=" + createdAt +
                ", bannerUrl='" + bannerUrl + '\'' +
                '}';
    }
}
