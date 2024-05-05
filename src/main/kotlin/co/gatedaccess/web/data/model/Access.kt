package co.gatedaccess.web.data.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.google.cloud.Timestamp
import com.mongodb.lang.NonNull
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Size
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.FieldType
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.util.Date

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
class Access {

    @Id var id: AccessId? = null

    @field:Size(min = 1, message = "Head count must be at least 1")
    @field:Field("head_count")
    var headCount: Int = 1

    @DBRef
    @field:NonNull
    @Indexed
    var host: Member? = null

    @field:Indexed
    @field:Field("checked_in_at")
    var checkedInAt: Date? = null

    @field:Field("checked_out_at")
    val checkedOutAt: Date? = null

    @DBRef
    @field:Field("checked_in_by")
    var checkedInBy : SecurityGuard? = null

    @DBRef
    @field:Field("checked_out_by")
    val checkedOutBy : SecurityGuard? = null

    @field:Field(value = "duration_of_visit", targetType = FieldType.TIMESTAMP)
    val durationOfVisit: Duration? = null

    @field:Indexed
    @field:NonNull
    @field:Future(message = "Valid until must be in the future")
    @field:Field("valid_until")
    var validUntil: Date = Date.from(Instant.from(LocalDateTime.now().plusHours(24)))

    @field:Field("created_at")
    @field:CreatedDate
    val createdAt: Date? = null

    @JsonIgnore
    @field:Field("updated_at")
    @field:LastModifiedDate
    val updatedAt: Date? = null
}