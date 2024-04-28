package co.gatedaccess.web.data.model

import jakarta.validation.constraints.Future
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document("member_phone_otp")
open class MemberPhoneOtp(phone: String, ref: String, expiry: Date) {

    @Id
    var id: String? = null
    var phone: String? = phone
    @Indexed(unique = true)
    var ref : String? = ref
    @Field("expire_at")
    var expireAt: @Future Date? = null

    @CreatedDate
    @Field("created_at")
    var createdAt: Date? = null
}
