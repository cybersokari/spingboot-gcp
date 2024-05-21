package ng.cove.web.data.repo

import ng.cove.web.data.model.Member
import ng.cove.web.util.CacheNames
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository

interface MemberRepo : MongoRepository<Member, String?> {

    @Cacheable(value = [CacheNames.MEMBERS])
    fun findMemberById(id: String): Member?
    fun findByPhoneAndCommunityIsNotNull(phone: String): Member?
    fun findByPhone(phone: String): Member?
    fun existsByPhoneAndCommunityIsNotNull(phone: String): Boolean
    fun findFirstByTestOtpIsNotNullAndPhone(phone: String): Member?
    fun findByIdAndTestOtp(id: String, testOtp: String): Member?
}
