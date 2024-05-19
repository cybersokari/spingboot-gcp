package ng.cove.web.data.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document("phone_otp")
class PhoneOtp {

    constructor(phone: String, ref: String, type: UserType, expireAt: Date?){
        this.phone = phone
        this.ref = ref
        this.type = type
        this.expireAt = expireAt
    }

    @Id
    var id: String? = null

    var phone: String? = null

    @Indexed(unique = true, sparse = true)
    var ref: String? = null

    private var type: UserType = UserType.Member

    @field:Field("expire_at")
    var expireAt: Date? = null

    @CreatedDate
    @Field("created_at")
    var createdAt: Date? = null
}
