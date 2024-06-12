package ng.cove.web.data.model

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.mongodb.lang.NonNull
import jakarta.validation.constraints.Size
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document(collection = "communities")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
open class Community {
    @Id
    var id: String? = null
    var name: @Size(max = 255) String? = null
    var address: @Size(max = 255) String? = null
    var country: @Size(max = 255) String? = null
    var state: @Size(max = 255) String? = null
    var desc: @Size(max = 255) String? = null

    @Field("super_admin_id")
    @Indexed(unique = true)
    var superAdminId: String? = null

    @NonNull
    @Indexed
    @Field("admins")
    var admins: Set<String> = setOf()

    @NonNull
    @Indexed
    @Field("members")
    var members: Set<String> = setOf()

    @NonNull
    @Indexed
    @Field("guards")
    var guards: Set<String> = setOf()

    @Field("created_at")
    @CreatedDate
    var createdAt: Date? = null

    @Field("banner_url")
    var bannerUrl: String? = null

    @Field("last_modified_at")
    @LastModifiedDate
    var lastModifiedAt: Date? = null

}
