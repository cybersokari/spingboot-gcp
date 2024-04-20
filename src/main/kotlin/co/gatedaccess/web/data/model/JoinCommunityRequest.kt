package co.gatedaccess.web.data.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document("join_community_request")
class JoinCommunityRequest {
    @Id
    var id: String? = null

    @DBRef
    var member: Member? = null

    @DBRef
    var community: Community? = null

    @Field("created_at")
    @CreatedDate
    var createdAt: Date? = null

    @Field("accepted_at")
    var acceptedAt: Date? = null

    @Field("rejected_at")
    var rejectedAt: Date? = null

    @DBRef
    var referrer: Member? = null

    class Builder {
        private var id: String? = null
        private var member: Member? = null
        private var community: Community? = null
        private var createdAt: Date? = null
        private var acceptedAt: Date? = null
        private var rejectAt: Date? = null
        private var referrer: Member? = null

        fun withId(id: String?): Builder {
            this.id = id
            return this
        }

        fun withMember(member: Member?): Builder {
            this.member = member
            return this
        }

        fun withCommunity(community: Community?): Builder {
            this.community = community
            return this
        }

        fun withCreatedAt(createdAt: Date?): Builder {
            this.createdAt = createdAt
            return this
        }

        fun withAcceptedAt(acceptedAt: Date?): Builder {
            this.acceptedAt = acceptedAt
            return this
        }

        fun withRejectAt(rejectAt: Date?): Builder {
            this.rejectAt = rejectAt
            return this
        }

        fun withReferrer(referrer: Member?): Builder {
            this.referrer = referrer
            return this
        }

        fun build(): JoinCommunityRequest {
            val joinCommunityRequest = JoinCommunityRequest()
            joinCommunityRequest.acceptedAt = acceptedAt
            joinCommunityRequest.rejectedAt = rejectAt
            joinCommunityRequest.member = this.member
            joinCommunityRequest.id = this.id
            joinCommunityRequest.community = this.community
            joinCommunityRequest.createdAt = this.createdAt
            joinCommunityRequest.referrer = this.referrer
            return joinCommunityRequest
        }

        companion object {
            fun aJoinCommunityRequest(): Builder {
                return Builder()
            }
        }
    }
}
