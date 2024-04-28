package co.gatedaccess.web.data.repo

import co.gatedaccess.web.data.model.Member
import org.springframework.data.mongodb.repository.MongoRepository

interface MemberRepo : MongoRepository<Member?, String?> {
    fun findMemberById(userId: String): Member?
    fun findByPhoneAndCommunityIsNotNull(phone: String): Member?
    fun findByPhone(phone: String): Member?
    fun existsByPhoneAndCommunityIsNotNull(phone: String): Boolean
}
