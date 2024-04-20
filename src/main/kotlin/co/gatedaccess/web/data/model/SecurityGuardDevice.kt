package co.gatedaccess.web.data.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.mongodb.lang.NonNull
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.MongoId
import java.util.*

@Document("security_guard_device")
class SecurityGuardDevice {
    @MongoId
    var id: String? = null

    @JsonProperty("device_name")
    @Field("device_name")
    var deviceName: String? = null

    @get:NonNull
    @NonNull
    @Indexed
    @Field("community_id")
    @JsonProperty("community_id")
    var communityId: String? = null

    @CreatedDate
    @Field("created_at")
    var createdAt: Date? = null

    class Builder {
        private var deviceName: String? = null
        private var communityId: String? = null

        fun withDeviceName(deviceName: String?): Builder {
            this.deviceName = deviceName
            return this
        }

        fun withCommunityId(communityId: String?): Builder {
            this.communityId = communityId
            return this
        }

        fun build(): SecurityGuardDevice {
            val securityGuardDevice = SecurityGuardDevice()
            securityGuardDevice.communityId = this.communityId
            securityGuardDevice.deviceName = this.deviceName
            return securityGuardDevice
        }

        companion object {
            fun aSecurityGuardDevice(): Builder {
                return Builder()
            }
        }
    }
}
