package ng.cove.web.data.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.mongodb.lang.NonNull
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document(collection = "members")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
open class Member: User {
    @Id
    override var id: String? = null

    @Field("first_name")
    @NonNull
    override var firstName: String? = null

    @Field("last_name")
    @NonNull
    override var lastName: String? = null

    @NonNull
    var gender: String? = null

    @Indexed(unique = true)
    override var phone: String? = null

    @Field(value = "phone_verified_at")
    override var phoneVerifiedAt: Date? = null

    var address: String? = null

    @Field(value = "photo_url")
    override var photoUrl: String? = null

    @NonNull
    @Indexed
    @Field("community_id")
    override var communityId: String? = null

    @CreatedDate
    @Field("created_at")
    override var createdAt: Date? = null

    @LastModifiedDate
    @Field("last_modified_at")
    override var lastModifiedAt: Date? = null

    /** Device info start**/
    @Field("device_name")
    override var deviceName: String? = null

    @JsonIgnore
    @Field("fcm_token")
    override var fcmToken: String? = null

    @Field("last_login_at")
    override var lastLoginAt: Date? = null

    /** Device info end**/

    @JsonIgnore
    @Field("test_otp")
    override var testOtp: String? = null
}
