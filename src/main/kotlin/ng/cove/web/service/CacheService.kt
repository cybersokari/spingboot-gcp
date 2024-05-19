package ng.cove.web.service

import ng.cove.web.data.model.Member
import ng.cove.web.data.model.SecurityGuard
import ng.cove.web.data.repo.MemberRepo
import ng.cove.web.data.repo.SecurityGuardRepo
import ng.cove.web.util.CacheNames
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class CacheService {
    @Autowired
    lateinit var memberRepo : MemberRepo
    @Autowired
    lateinit var guardRepo : SecurityGuardRepo

    @Cacheable(value = [CacheNames.MEMBERS])
    fun getMemberById(id: String): Member? {
        return memberRepo.findById(id).orElse(null)
    }

    @Cacheable(value = [CacheNames.GUARDS])
    fun getGuardById(id: String): SecurityGuard? {
        return guardRepo.findById(id).orElse(null)
    }
}