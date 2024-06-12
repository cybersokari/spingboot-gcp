package ng.cove.web.data.repo

import ng.cove.web.data.model.User

interface UserRepo {
    fun findFirstById(id: String): User?
    fun findByPhoneAndCommunityIdIsNotNull(phone: String): User?
    fun findByPhoneAndCommunityId(phone: String, communityId: String): User?
//    fun save(user: User)
}