package ng.cove.web.data.repo

import ng.cove.web.data.model.Member
import ng.cove.web.data.model.SecurityGuard
import ng.cove.web.util.CacheNames
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.mongodb.repository.MongoRepository

interface SecurityGuardRepo : MongoRepository<SecurityGuard?, String?>{
    @Cacheable(value = [CacheNames.GUARDS])
    fun findSecurityGuardById(id: String): SecurityGuard?
    fun findByPhoneAndCommunityIdIsNotNull(phone: String): SecurityGuard?
    fun existsByPhone(phone: String): Boolean
    fun findByPhone(phone: String): SecurityGuard?
    fun findFirstByTestOtpIsNotNullAndPhone(phone: String): SecurityGuard?
    fun findByIdAndTestOtp(id: String, testOtp: String): SecurityGuard?
}
