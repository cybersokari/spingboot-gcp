package ng.cove.web.data.model

import com.mongodb.lang.NonNull
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document("phone_otp")
@CompoundIndex(name = "phone_ref", def = "{'phone': 1, 'ref': 1}", unique = true)
class PhoneOtp {

    @Id
    var id: String? = null

    @field:NonNull
    var phone: String? = null

    @field:NonNull
    var ref: String? = null

    var type: UserType = UserType.MEMBER

    @field:Field("expire_at")
    var expireAt: Date? = null

    @CreatedDate
    @Field("created_at")
    var createdAt: Date? = null
}
