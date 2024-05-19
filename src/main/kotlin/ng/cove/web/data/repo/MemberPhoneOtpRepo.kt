package ng.cove.web.data.repo

import ng.cove.web.data.model.PhoneOtp
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.Date

interface MemberPhoneOtpRepo: MongoRepository<PhoneOtp, String> {
    fun countByPhoneAndCreatedAtIsAfter(phone: String, createdAt: Date): Long
    fun countByPhone(phone: String): Long
}