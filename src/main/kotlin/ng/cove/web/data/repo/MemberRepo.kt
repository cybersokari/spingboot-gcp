package ng.cove.web.data.repo

import ng.cove.web.data.model.Member
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository

interface MemberRepo : MongoRepository<Member, String?> {

    fun findByPhoneAndCommunityIsNotNull(phone: String): Member?
    fun findByPhone(phone: String): Member?
    fun existsByPhoneAndCommunityIsNotNull(phone: String): Boolean
    fun findByFirstNameEquals(name: String, pageable: Pageable): Page<Member>
    fun findFirstByTestOtpIsNotNullAndPhone(phone: String): Member?
    fun findByIdAndTestOtp(id: String, testOtp: String): Member?
}
