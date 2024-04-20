package co.gatedaccess.web.data.repo

import co.gatedaccess.web.data.model.SecurityGuardOtp
import org.springframework.data.mongodb.repository.MongoRepository

interface SecurityGuardOtpRepo : MongoRepository<SecurityGuardOtp?, String?> {
    fun findByCode(code: String?): SecurityGuardOtp?
    fun findByCommunityId(communityId: String?): SecurityGuardOtp?
    fun deleteByCommunityId(communityId: String?)
}
