package ng.cove.web.data.repo

import ng.cove.web.data.model.MemberPhoneOtp
import org.springframework.data.mongodb.repository.MongoRepository

interface MemberPhoneOtpRepo: MongoRepository<MemberPhoneOtp, String> {
    fun findByRef(ref: String): MemberPhoneOtp?
}