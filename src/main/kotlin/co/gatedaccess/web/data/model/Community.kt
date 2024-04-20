package co.gatedaccess.web.data.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Size
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document
class Community {
    @Id
    var id: String? = null
    var name: @Size(max = 255) String? = null
    var address: @Size(max = 255) String? = null
    var country: @Size(max = 255) String? = null
    var state: @Size(max = 255) String? = null
    var desc: @Size(max = 255) String? = null

    @DBRef(lazy = true)
    @Field("super_admin")
    @JsonProperty("super_admin")
    var superAdmin: Member? = null

    @Field("created_at")
    @JsonProperty("created_at")
    @CreatedDate
    var createdAt: Date? = null

    @Field("banner_url")
    @JsonProperty("banner_url")
    var bannerUrl: String? = null

    @Field("last_modified_at")
    @LastModifiedDate
    var lastModifiedAt: Date? = null

    constructor()

    constructor(
        id: String?,
        name: String?,
        address: String?,
        country: String?,
        state: String?,
        desc: String?,
        superAdmin: Member?,
        createdAt: Date?,
        bannerUrl: String?
    ) {
        this.id = id
        this.name = name
        this.address = address
        this.country = country
        this.state = state
        this.desc = desc
        this.superAdmin = superAdmin
        this.createdAt = createdAt
        this.bannerUrl = bannerUrl
    }

    override fun toString(): String {
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
                '}'
    }
}
