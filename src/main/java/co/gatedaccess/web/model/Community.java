package co.gatedaccess.web.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.Date;

public class Community {
    @Id
    private String id;
    private String name;
    private String address;
    private String country;
    private String state;
    private String desc;
    private ObjectId superAdmin;
    private Date createdAt;
    private String bannerUrl;

    public Community() {
    }

    public Community(String id, String name, String address, String country, String state, String desc, ObjectId superAdmin, Date createdAt, String bannerUrl) {
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

    public ObjectId getSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(ObjectId superAdmin) {
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
}
