package ng.cove.web.data.repo

import ng.cove.web.data.model.Community
import org.springframework.data.mongodb.repository.MongoRepository

interface CommunityRepo : MongoRepository<Community?, String?> {
    fun findByAdminIdsContains(adminId: String): Community?
    fun findCommunityByIdAndAdminIdsContains(id: String, adminId: String): Community?
}
