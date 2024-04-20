package co.gatedaccess.web.data.repo

import co.gatedaccess.web.data.model.JoinCommunityRequest
import org.springframework.data.mongodb.repository.MongoRepository

interface JoinCommunityRequestRepo : MongoRepository<JoinCommunityRequest?, String?> {
    fun findJoinCommunityRequestByMemberIdAndAcceptedAtIsNull(memberId: String?): JoinCommunityRequest?
    fun existsJoinCommunityRequestByMemberIdAndAcceptedAtIsNotNull(memberId: String?): Boolean
    fun findJoinCommunityRequestById(id: String?): JoinCommunityRequest?
}
