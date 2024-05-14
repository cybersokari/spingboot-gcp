package ng.cove.web.data.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.mongodb.lang.NonNull
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document("security_guard")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
class SecurityGuard {
    @Id
    var id: String? = null

    @Field("first_name")
    @NonNull
    var firstName: String? = null

    @Field("last_name")
    @NonNull
    var lastName: String? = null

    @NonNull
    @Indexed(unique = true)
    var phone: String? = null

    @Field("phone_verified_at")
    var phoneVerifiedAt: Date? = null

    @NonNull
    @Indexed
    @Field("community_id")
    var communityId: String? = null

    @CreatedDate
    @Field("created_at")
    var createdAt: Date? = null

    /** Device info start**/
    @Field("device_name")
    var deviceName: String? = null

    @JsonIgnore
    @Indexed(unique = true, sparse = true)
    @Field("device_id")
    var deviceId: String? = null

    @JsonIgnore
    @Field("fcm_token")
    var fcmToken: String? = null

    @Field("last_login_at")
    var lastLoginAt: Date? = null
    /** Device info end**/

    @JsonIgnore
    @Field("test_otp")
    var testOtp: String? = null

}
