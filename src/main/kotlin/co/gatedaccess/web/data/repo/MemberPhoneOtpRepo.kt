package co.gatedaccess.web.data.repo

import co.gatedaccess.web.data.model.MemberPhoneOtp
import org.springframework.data.mongodb.repository.MongoRepository

interface MemberPhoneOtpRepo: MongoRepository<MemberPhoneOtp, String> {
    fun findByRef(ref: String): MemberPhoneOtp?
}