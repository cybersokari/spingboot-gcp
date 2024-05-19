package ng.cove.web.http.controller


import com.google.firebase.auth.FirebaseToken
import ng.cove.web.AppTests
import ng.cove.web.data.model.*
import ng.cove.web.service.CacheService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import org.mockito.kotlin.*
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.post
import java.util.*

@ActiveProfiles("test")
class AdminControllerTest : AppTests() {
    @MockBean
    lateinit var cacheService: CacheService

    @Test
    fun givenUserNoJWT_WhenDeleteGuard_ThenReturns401() {
        val result = mockMvc.delete("/admin/guard/{guard_id}", 1).andReturn().response
        assertEquals(401, result.status)
        verifyNoInteractions(auth)
        verify(cacheService, never()).getMemberById(member.id!!)
    }

    @Test
    fun givenUserInvalidJWT_WhenDeleteGuard_ThenReturns401() {

        `when`(auth.verifyIdToken(any(), any())).thenReturn(null)

        val unknownToken = faker.random().hex(21)
        val result = mockMvc.delete("/admin/guard/{guard_id}", 1) {
            header("Authorization", "Bearer $unknownToken")
        }.andReturn().response

        assertEquals(401, result.status)
        verify(auth, times(1)).verifyIdToken(unknownToken, true)
        verify(cacheService, never()).getMemberById(member.id!!)
    }

    @Nested
    inner class AuthorizedAdminTest {

        private val idToken: String = faker.random().hex(30)
        private val firebaseToken: FirebaseToken = Mockito.mock(FirebaseToken::class.java)

        @BeforeEach
        fun setUp() {
            //auth user
            reset(auth)
            reset(cacheService)
            `when`(auth.verifyIdToken(idToken, true)).thenReturn(firebaseToken)
            `when`(firebaseToken.claims).thenReturn(mapOf("type" to UserType.Member.name))
            `when`(firebaseToken.uid).thenReturn(member.id)
            `when`(cacheService.getMemberById(member.id!!)).thenReturn(member)
        }


        @Test
        fun givenUserNotAdmin_WhenDeleteGuard_ThenReturns401() {
            community.adminIds = setOf("some_id")
            member.community = community

            val result = mockMvc.delete("/admin/guard/{guard_id}", 1) {
                header("Authorization", "Bearer $idToken")
            }.andReturn().response
            assertEquals(401, result.status)
            verify(firebaseToken, times(1)).uid
            verify(firebaseToken, times(1)).claims
            verify(cacheService, times(1)).getMemberById(member.id!!)
        }

        @Test
        fun givenUserAdmin_whenAcceptJoinRequest_thenReturns200() {
            val newMember = Member().apply {
                id = faker.random().hex(20)
                firstName = faker.name().firstName()
                lastName = faker.name().lastName()
                phone = faker.phoneNumber().phoneNumberInternational()
                address = faker.address().fullAddress()
            }

            communityRepo.save(community)

            val requestId = JoinRequestId().apply {
                communityId = community.id!!
                phone = newMember.phone!!
            }
            val request = JoinRequest().apply {
                firstName = newMember.firstName
                lastName = newMember.lastName
                id = requestId
                address = newMember.address
                referrerId = member.id
                gender = Gender.female
            }

            joinRequestRepo.save(request)

            val result = mockMvc.post("/admin/community/request/{accept}", true) {
                header("Authorization", "Bearer $idToken")
                content = mapper.writeValueAsString(requestId)
                contentType = MediaType.APPLICATION_JSON
            }.andReturn().response
            assertEquals(200, result.status)
        }

    }

}