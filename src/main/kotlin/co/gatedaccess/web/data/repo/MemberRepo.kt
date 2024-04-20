package co.gatedaccess.web.data.repo

import co.gatedaccess.web.data.model.Member
import org.springframework.data.mongodb.repository.MongoRepository

interface MemberRepo : MongoRepository<Member?, String?> {
    fun findMemberById(userId: String?): Member

    fun findMemberByInviteCode(inviteCode: String?): Member?

    fun findMemberByEmail(email: String?): Member?

    fun existsMemberByEmail(email: String?): Boolean
}
