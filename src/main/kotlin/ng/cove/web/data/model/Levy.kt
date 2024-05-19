package ng.cove.web.data.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.mongodb.lang.NonNull
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document
@CompoundIndex(name = "community_id_title", def = "{'community_id': 1, 'title': 1}", unique = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
class Levy {
    @Id
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var id: String? = null

    @field:NonNull
    var title: String? = null

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @field:Indexed
    @field:NonNull
    @Field("community_id")
    var communityId: String? = null

    @field:NonNull
    @field:NotNull
    @Min(1, message = "Amount must be greater than 0")
    @Field("amount")
    var amount: Double? = null

    @field:NonNull
    @field:NotNull
    var type: LevyType? = null

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @CreatedDate
    @Field("created_at")
    var createdAt: Date? = null

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Field("updated_at")
    @LastModifiedDate
    var updatedAt: Date? = null
}