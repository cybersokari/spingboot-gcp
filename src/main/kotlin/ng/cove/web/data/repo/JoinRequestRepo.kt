package ng.cove.web.data.repo

import ng.cove.web.data.model.JoinRequest
import org.springframework.data.mongodb.repository.MongoRepository

interface JoinRequestRepo : MongoRepository<JoinRequest, String?> {
    fun existsByPhoneAndCommunityId(phone: String, communityId: String): Boolean
}
