package ng.cove.web.data.repo

import ng.cove.web.data.model.Community
import org.springframework.data.mongodb.repository.MongoRepository

interface CommunityRepo : MongoRepository<Community?, String?> {
    fun findCommunityById(id: String?): Community?

    fun findCommunityBySuperAdminId(id: String?): Community?
    fun findCommunityByIdAndSuperAdminId(id: String, superAdminId: String): Community?
    fun findByAdminIdsContains(adminId: String): Community?
    fun  existsByAdminIdsContains(adminId: String): Boolean
}
