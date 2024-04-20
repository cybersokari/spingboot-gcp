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
    @field:Id var id: String,
    @field:Field(
        "first_name"
    ) @field:Indexed var firstName: String,
    @field:Field(
        "last_name"
    ) @field:Indexed var lastName: String,
    var phone: String,
    var gender: String,
    @field:DBRef(
        lazy = true
    ) @field:NonNull @param:NonNull var guardian: Member,
    @field:Field(
        "photo_url"
    ) var photoUrl: String,
    @field:CreatedDate @field:JsonProperty("created_at") @field:Field("created_at") var createdAt: Date
)
