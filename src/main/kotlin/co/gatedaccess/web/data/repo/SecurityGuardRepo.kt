package co.gatedaccess.web.data.repo

import co.gatedaccess.web.data.model.SecurityGuard
import org.springframework.data.mongodb.repository.MongoRepository

interface SecurityGuardRepo : MongoRepository<SecurityGuard?, String?>{
    fun findByPhoneAndCommunityIdIsNotNull(phone: String): SecurityGuard?
    fun existsByPhoneAndCommunityIdIsNotNull(phone: String): Boolean
    fun existsByPhone(phone: String): Boolean
    fun findByPhone(phone: String): SecurityGuard?
}
