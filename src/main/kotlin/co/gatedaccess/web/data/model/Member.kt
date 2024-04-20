package co.gatedaccess.web.data.model

import com.fasterxml.jackson.annotation.JsonProperty
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
class Member(
    @field:Id var id: String?,
    @field:Field(
        "first_name"
    ) @field:JsonProperty(
        "first_name"
    ) @field:NonNull @param:NonNull var firstName: String?,
    @field:Field(
        "last_name"
    ) @field:JsonProperty(
        "last_name"
    ) @field:NonNull @param:NonNull var lastName: String?,
    @field:NonNull @param:NonNull var gender: String?,
    @field:Indexed(
        unique = true
    ) @field:NonNull @param:NonNull var email: String?,
    @field:Indexed(
        unique = true
    ) var phone: String?,
    @field:Field(value = "phone_verified_at") @field:JsonProperty("phone_verified_at") var phoneVerifiedAt: Date?,
    @field:Field(
        value = "email_verified_at"
    ) @field:JsonProperty(
        "email_verified_at"
    ) var emailVerifiedAt: Date?,
    var address: String?,
    @field:Field(
        value = "photo_url"
    ) @field:JsonProperty(
        "photo_url"
    ) var photoUrl: String?,
    @field:DBRef(lazy = true) var community: Community?,
    @field:DBRef var device: Device?,
    @field:Field(
        "invite_code"
    ) @field:Indexed(
        unique = true
    ) @field:JsonProperty(
        "invite_code"
    ) @field:NonNull @param:NonNull var inviteCode: String?,
    @field:CreatedDate @field:Field(
        "created_at"
    ) @field:JsonProperty(
        "created_at"
    ) var createdAt: Date?,
    @field:LastModifiedDate @field:Field("last_modified_at") @field:JsonProperty("last_modified_at") var lastModifiedAt: Date?,
    @field:Field(
        "google_id"
    ) @field:JsonProperty(
        "google_id"
    ) @field:Indexed(
        unique = true,
        sparse = true
    ) var googleUserId: String?,
    @field:Field("apple_id") @field:Indexed(
        unique = true,
        sparse = true
    ) @field:JsonProperty("apple_id") var appleUserId: String?
) {
    class Builder {
        private var id: String? = null
        private var firstName: String? = null
        private var lastName: String? = null
        private var gender: String? = null
        private var email: String? = null
        private var phone: String? = null
        private var phoneVerifiedAt: Date? = null
        private var emailVerifiedAt: Date? = null
        private var address: String? = null
        private var photoUrl: String? = null
        private var community: Community? = null
        private var device: Device? = null
        private var inviteCode: String? = null
        private var createdAt: Date? = null
        private var lastModifiedAt: Date? = null
        private var googleUserId: String? = null
        private var appleUserId: String? = null

        fun withId(id: String?): Builder {
            this.id = id
            return this
        }

        fun withFirstName(firstName: String?): Builder {
            this.firstName = firstName
            return this
        }

        fun withLastName(lastName: String?): Builder {
            this.lastName = lastName
            return this
        }

        fun withGender(gender: String?): Builder {
            this.gender = gender
            return this
        }

        fun withEmail(email: String?): Builder {
            this.email = email
            return this
        }

        fun withPhone(phone: String?): Builder {
            this.phone = phone
            return this
        }

        fun withPhoneVerifiedAt(phoneVerifiedAt: Date?): Builder {
            this.phoneVerifiedAt = phoneVerifiedAt
            return this
        }

        fun withEmailVerifiedAt(emailVerifiedAt: Date?): Builder {
            this.emailVerifiedAt = emailVerifiedAt
            return this
        }

        fun withAddress(address: String?): Builder {
            this.address = address
            return this
        }

        fun withPhotoUrl(photoUrl: String?): Builder {
            this.photoUrl = photoUrl
            return this
        }

        fun withCommunity(community: Community?): Builder {
            this.community = community
            return this
        }

        fun withDevice(device: Device?): Builder {
            this.device = device
            return this
        }

        fun withInviteCode(inviteCode: String?): Builder {
            this.inviteCode = inviteCode
            return this
        }

        fun withCreatedAt(createdAt: Date?): Builder {
            this.createdAt = createdAt
            return this
        }

        fun withLastModifiedAt(lastModifiedAt: Date?): Builder {
            this.lastModifiedAt = lastModifiedAt
            return this
        }

        fun withGoogleUserId(googleUserId: String?): Builder {
            this.googleUserId = googleUserId
            return this
        }

        fun withAppleUserId(appleUserId: String?): Builder {
            this.appleUserId = appleUserId
            return this
        }

        fun build(): Member {
            return Member(
                id,
                firstName,
                lastName,
                gender,
                email,
                phone,
                phoneVerifiedAt,
                emailVerifiedAt,
                address,
                photoUrl,
                community,
                device,
                inviteCode,
                createdAt,
                lastModifiedAt,
                googleUserId,
                appleUserId
            )
        }

        companion object {
            fun aMember(): Builder {
                return Builder()
            }
        }
    }
}
