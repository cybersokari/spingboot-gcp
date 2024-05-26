package ng.cove.web.data.repo

import ng.cove.web.data.model.Community
import org.springframework.data.mongodb.repository.MongoRepository

interface CommunityRepo : MongoRepository<Community, String> {
    fun findByAdminsContains(adminId: String): Community?
    fun findCommunityByIdAndAdminsContains(id: String, adminId: String): Community?
    fun existsByAdminsContains(adminId: String): Boolean
}
