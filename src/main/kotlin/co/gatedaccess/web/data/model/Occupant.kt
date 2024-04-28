package co.gatedaccess.web.data.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.mongodb.lang.NonNull
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document
class Occupant(
    @Id var id: String,
    @Field(
        "first_name"
    ) @field:Indexed var firstName: String,
    @Field(
        "last_name"
    ) @Indexed var lastName: String,
    var phone: String,
    var gender: String,
    @DBRef(
        lazy = true
    ) @field:NonNull @param:NonNull var guardian: Member,
    @field:Field(
        "photo_url"
    ) var photoUrl: String,
    @field:CreatedDate @field:JsonProperty("created_at") @field:Field("created_at") var createdAt: Date
)
