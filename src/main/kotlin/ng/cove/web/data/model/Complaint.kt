package ng.cove.web.data.model

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.mongodb.lang.NonNull
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.MongoId
import java.util.*

@Document(collection = "complaints")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
class Complaint {
    @MongoId
    var id: String? = null

    @field:NonNull
    @Field("member_id")
    var memberId: String? = null

    @field:NonNull
    @Field("complaint_type")
    var complaintType: String? = null

    @field:NonNull
    @Field("description")
    var description: String? = null

    @Field("images")
    var images : Set<String>? = null

    @field:NonNull
    @Field("created_at")
    @CreatedDate
    var createdAt: Date? = null

    @Field("resolved_at")
    var resolvedAt: Date? = null

    @Field("last_update_at")
    @LastModifiedDate
    var lastUpdateAt : Date? = null

    @field:NonNull
    @Field("status")
    var status: ComplaintStatus = ComplaintStatus.PENDING
}

enum class ComplaintStatus {
    PENDING,
    RESOLVED
}