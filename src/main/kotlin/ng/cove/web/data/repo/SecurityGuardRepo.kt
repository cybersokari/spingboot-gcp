package ng.cove.web.data.repo

import ng.cove.web.data.model.Member
import ng.cove.web.data.model.SecurityGuard
import org.springframework.data.mongodb.repository.MongoRepository

interface SecurityGuardRepo : MongoRepository<SecurityGuard?, String?>{
    fun findByPhoneAndCommunityIdIsNotNull(phone: String): SecurityGuard?
    fun existsByPhone(phone: String): Boolean
    fun findByPhone(phone: String): SecurityGuard?
    fun findFirstByTestOtpIsNotNullAndPhone(phone: String): SecurityGuard?
    fun findByIdAndTestOtp(id: String, testOtp: String): SecurityGuard?
}
