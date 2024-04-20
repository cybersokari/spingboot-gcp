package co.gatedaccess.web.data.model

import com.mongodb.lang.NonNull
import jakarta.validation.constraints.Future
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.MongoId
import java.util.*

@Document("security_guard_otp")
class SecurityGuardOtp {
    @MongoId
    var id: String? = null

    @JvmField
    @NonNull
    @Indexed(unique = true)
    var code: String? = null

    @JvmField
    @NonNull
    @Indexed(unique = true)
    var communityId: String? = null

    @JvmField
    @NonNull
    @Field("expire_at")
    var expireAt: @Future Date? = null

    constructor()

    @CreatedDate
    @Field("created_at")
    var createdAt: Date? = null

    constructor(
        id: String?,
        @NonNull code: String?,
        @NonNull communityId: String?,
        @NonNull expireAt: Date?,
        createdAt: Date?
    ) {
        this.id = id
        this.code = code
        this.communityId = communityId
        this.expireAt = expireAt
        this.createdAt = createdAt
    }

    fun getCode(): String? {
        return code
    }

    fun getCommunityId(): String? {
        return communityId
    }

    @NonNull
    fun getExpireAt(): Date? {
        return expireAt
    }

    fun setCode(@NonNull code: String?) {
        this.code = code
    }

    fun setCommunityId(@NonNull communityId: String?) {
        this.communityId = communityId
    }

    fun setExpireAt(@NonNull expireAt: Date?) {
        this.expireAt = expireAt
    }

    class Builder {
        private var expireAt: Date? = null
        private var communityId: String? = null
        private var code: String? = null

        fun withExpireAt(expireAt: Date?): Builder {
            this.expireAt = expireAt
            return this
        }

        fun withCommunityId(communityId: String?): Builder {
            this.communityId = communityId
            return this
        }

        fun withCode(code: String?): Builder {
            this.code = code
            return this
        }

        fun build(): SecurityGuardOtp {
            val securityGuardOtp = SecurityGuardOtp()
            securityGuardOtp.communityId = this.communityId
            securityGuardOtp.expireAt = this.expireAt
            securityGuardOtp.code = this.code
            return securityGuardOtp
        }

        companion object {
            fun aSecurityGuardOtp(): Builder {
                return Builder()
            }
        }
    }
}
