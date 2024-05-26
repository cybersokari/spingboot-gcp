package ng.cove.web.data.repo

import ng.cove.web.data.model.Member
import ng.cove.web.util.CacheName
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.mongodb.repository.MongoRepository

interface MemberRepo : MongoRepository<Member, String?>, UserRepo {

    @Cacheable(value = [CacheName.MEMBERS])
    override fun findFirstById(id: String) : Member?
    override fun findByPhoneAndCommunityIdIsNotNull(phone: String): Member?
    override fun findByPhoneAndCommunityId(phone: String, communityId: String): Member?
    @Cacheable(value = [CacheName.MEMBERS])
    fun findByPhone(phone: String): Member?
    fun existsByPhoneAndCommunityIdIsNotNull(phone: String): Boolean
    fun findFirstByTestOtpIsNotNullAndPhone(phone: String): Member?
    fun findByIdAndTestOtp(id: String, testOtp: String): Member?
}
