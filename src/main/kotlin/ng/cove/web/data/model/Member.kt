package ng.cove.web.data.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.mongodb.lang.NonNull
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
open class Member {
    @Id
    var id: String? = null

    @Field("first_name")
    @NonNull
    var firstName: String? = null

    @Field("last_name")
    @NonNull
    var lastName: String? = null

    @NonNull
    var gender: String? = null

    @Indexed(unique = true)
    var phone: String? = null

    @Field(value = "phone_verified_at")
    var phoneVerifiedAt: Date? = null

    var address: String? = null

    @Field(value = "photo_url")
    var photoUrl: String? = null

    @DBRef
    @NonNull
    var community: Community? = null

    @CreatedDate
    @Field("created_at")
    var createdAt: Date? = null

    @LastModifiedDate
    @Field("last_modified_at")
    var lastModifiedAt: Date? = null

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
