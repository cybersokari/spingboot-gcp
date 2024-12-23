package ng.cove.web.data.repo

import ng.cove.web.data.model.SecurityGuard
import ng.cove.web.util.CacheName
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.mongodb.repository.MongoRepository

interface SecurityGuardRepo : MongoRepository<SecurityGuard?, String?>, UserRepo{
    @Cacheable(value = [CacheName.GUARDS])
    override fun findFirstById(id: String) : SecurityGuard?

    override fun findByPhoneAndCommunityIdIsNotNull(phone: String): SecurityGuard?
    fun existsByPhone(phone: String): Boolean
    fun findByPhone(phone: String): SecurityGuard?
    fun findFirstByTestOtpIsNotNullAndPhone(phone: String): SecurityGuard?
    fun findByIdAndTestOtp(id: String, testOtp: String): SecurityGuard?
}
