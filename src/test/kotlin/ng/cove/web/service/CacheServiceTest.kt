package ng.cove.web.service

import ng.cove.web.AppTest
import ng.cove.web.data.model.Member
import ng.cove.web.util.CacheNames
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.caffeine.CaffeineCacheManager
import kotlin.test.assertEquals


class CacheServiceTest : AppTest() {

    @Autowired
    lateinit var cacheManager: CaffeineCacheManager

    @Autowired
    lateinit var cacheService: CacheService

    @Test
    fun givenMemberThatShouldBeCached_whenMemberIsQueriedById_thenMemberIsAddedToCache() {
        memberRepo.save(member)
        val id = member.id!!
        val cacheName = CacheNames.MEMBERS
        cacheManager.getCache(cacheName)?.clear()

        val queriedMember = cacheService.getMemberById(id)!!
        val cachedMember = cacheManager.getCache(cacheName)?.get(id, Member::class.java)

        assertEquals(cachedMember, queriedMember, "Member is cached after query")
    }
}