package co.gatedaccess.web.data.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document
class Device {
    @Id
    var id: String? = null
    var name: String? = null

    @Field("fcm_token")
    var fcmToken: String? = null

    @Indexed(unique = true)
    @Field("member_id")
    var memberId: String? = null

    @CreatedDate
    @Field("created_at")
    var createdAt: Date? = null
}
