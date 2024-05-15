package ng.cove.web.data.model

import com.mongodb.lang.NonNull
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.Date

@Document("assigned_levy")
class AssignedLevy {
    @Id
    var id: AssignedLevyId? = null

    @Field("assigned_by")
    @field:NonNull
    var assignedBy: String? = null

    @field:NonNull
    @Field("created_at")
    var createdAt: Date? = null
}