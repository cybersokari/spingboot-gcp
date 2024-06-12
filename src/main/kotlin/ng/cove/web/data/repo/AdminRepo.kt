package ng.cove.web.data.repo

import ng.cove.web.data.model.Admin
import ng.cove.web.util.CacheName
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.mongodb.repository.MongoRepository

interface AdminRepo : MongoRepository<Admin, String>,UserRepo{
    @Cacheable(value = [CacheName.ADMINS])
    override fun findFirstById(id: String) : Admin?
    override fun findByPhoneAndCommunityIdIsNotNull(phone: String): Admin?
    fun findByPhone(phone: String): Admin?
    fun findByIdAndTestOtp(id: String, testOtp: String): Admin?
    fun findFirstByTestOtpIsNotNullAndPhone(phone: String): Admin?
}