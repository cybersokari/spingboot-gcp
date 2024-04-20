package co.gatedaccess.web.data.model

import jakarta.validation.constraints.Future
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document("email_otp")
class EmailOtp {
    @Id
    var id: String? = null
    var code: String? = null
    var email: String? = null

    @Field("expire_at")
    var expireAt: @Future Date? = null
}
