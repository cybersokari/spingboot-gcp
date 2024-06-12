package ng.cove.web.service

import ng.cove.web.AppTest
import ng.cove.web.data.model.Member
import ng.cove.web.util.CacheName
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.caffeine.CaffeineCacheManager
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


class CacheTest : AppTest() {

    @Autowired
    lateinit var cacheManager: CaffeineCacheManager

    @Test
    fun givenMemberThatShouldBeCached_whenMemberIsQueriedById_thenMemberIsAddedToCache() {
        memberRepo.save(member)
        val id = member.id!!
        val cacheName = CacheName.MEMBERS
        cacheManager.getCache(cacheName)?.clear()
        var cachedMember = cacheManager.getCache(cacheName)?.get(id, Member::class.java)

        assertNull(cachedMember, "Cache is empty before query")

        val queriedMember = memberRepo.findFirstById(id)!!
        cachedMember = cacheManager.getCache(cacheName)?.get(id, Member::class.java)

        assertNotNull(cachedMember, "Cache is not empty before query")
        assertEquals(cachedMember, queriedMember, "Queried data is equal to cached data")
    }
}