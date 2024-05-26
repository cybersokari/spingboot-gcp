package ng.cove.web.http.controller


import com.google.firebase.auth.FirebaseToken
import ng.cove.web.AppTest
import ng.cove.web.data.model.*
import ng.cove.web.data.repo.AdminRepo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.reset
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.post
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ActiveProfiles("test")
class AdminControllerTest : AppTest() {

    @Autowired
    lateinit var adminRepo: AdminRepo

    @Test
    fun givenUserNoJWT_WhenDeleteGuard_ThenReturns401() {
        val result = mockMvc.delete("/admin/guard/{guard_id}", 1).andReturn().response
        assertEquals(401, result.status)
        verifyNoInteractions(auth)
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
    }

    @Nested
    inner class AuthorizedAdminTest {

        private lateinit var admin: Admin

        private val idToken: String = faker.random().hex(30)
        private val firebaseToken: FirebaseToken = Mockito.mock(FirebaseToken::class.java)

        @BeforeEach
        fun setUp() {
            admin = Admin().apply {
                firstName = faker.name().firstName()
                lastName = faker.name().lastName()
                phone = faker.phoneNumber().phoneNumberInternational()
            }
            admin.communityId = community.id
            admin = adminRepo.save(admin)
            //auth user
            reset(auth)
            `when`(auth.verifyIdToken(idToken, true)).thenReturn(firebaseToken)
            `when`(firebaseToken.claims).thenReturn(mapOf("type" to UserType.Admin.name))
            `when`(firebaseToken.uid).thenReturn(admin.id)
        }


        @Test
        fun givenUserNotAdmin_WhenDeleteGuard_ThenReturns401() {
            communityRepo.save(community)

            val result = mockMvc.delete("/admin/guard/{guard_id}", 1) {
                header("Authorization", "Bearer $idToken")
            }.andReturn().response
            assertEquals(401, result.status)
            verify(firebaseToken, times(1)).uid
            verify(firebaseToken, times(1)).claims
        }

        @Test
        fun givenUserAdmin_whenAcceptJoinRequest_thenReturns200() {
            val newMember = Member().apply {
                firstName = faker.name().firstName()
                lastName = faker.name().lastName()
                phone = faker.phoneNumber().cellPhone()
                address = faker.address().fullAddress()
            }
            val numberOfMembersInCommunity = community.members.size

            community.admins = setOf(admin.id!!)
            community = communityRepo.save(community)

            var request = JoinRequest().apply {
                firstName = newMember.firstName
                lastName = newMember.lastName
                communityId = community.id!!
                phone = newMember.phone!!
                address = newMember.address
                referrerId = member.id
                gender = Gender.FEMALE
            }

            request = joinRequestRepo.save(request)

            val result = mockMvc.post("/admin/community/request/{id}?accept={accept}", request.id, true) {
                header("Authorization", "Bearer $idToken")
                contentType = MediaType.APPLICATION_JSON
            }.andReturn().response

            community = communityRepo.findById(community.id!!).get()
            val createdMember = memberRepo.findByPhoneAndCommunityId(newMember.phone!!, community.id!!)

            assertEquals(200, result.status)
            assertNotNull(createdMember, "New member should be created")
            assertTrue(
                community.members.size > numberOfMembersInCommunity,
                "New member id should be added to community"
            )
        }

    }

}