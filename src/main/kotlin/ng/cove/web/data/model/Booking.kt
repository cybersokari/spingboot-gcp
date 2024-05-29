package ng.cove.web.data.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.mongodb.lang.NonNull
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotNull
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document(collection = "bookings")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@CompoundIndex(name = "code_community_id", def = "{'code': 1, 'community_id': 1}", unique = true)
class Booking {

    @Id
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var id: String? = null

    val name : String? = null

    @Field("code")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var code: String? = null

    @field:NonNull
    @field:Indexed
    @Field("community_id")
    @JsonIgnore
    var communityId: String? = null

    @field:Field("head_count")
    var headCount: Int = 1

    @field:NonNull
    @field:Indexed
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var host: String? = null

    @field:Indexed
    @field:Field("checked_in_at")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var checkedInAt: Date? = null

    @field:Field("checked_out_at")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var checkedOutAt: Date? = null

    @field:Field("checked_in_by")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var checkedInBy: String? = null

    @field:Field("checked_out_by")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var checkedOutBy: String? = null

    @FutureOrPresent
    @field:Indexed
    @field:NonNull
    @field:NotNull
    @Field(value = "time_of_entry")
    lateinit var timeOfEntry: Date

    @Future
    @field:Indexed
    @field:NonNull
    @field:NotNull
    @Field(value = "time_of_exit")
    lateinit var timeOfExit: Date

    @field:CreatedDate
    @field:Field("created_at")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var createdAt: Date? = null

    @JsonIgnore
    @field:Field("updated_at")
    @field:LastModifiedDate
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var updatedAt: Date? = null
}