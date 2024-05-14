package ng.cove.web.data.repo

import ng.cove.web.data.model.PhoneOtp
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.Date

interface MemberPhoneOtpRepo: MongoRepository<PhoneOtp, String> {
    fun countByCreatedAtIsAfter(createdAt: Date): Long
}