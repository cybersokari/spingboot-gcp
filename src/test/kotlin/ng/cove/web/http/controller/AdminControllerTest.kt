package ng.cove.web.http.controller


import com.google.firebase.auth.FirebaseToken
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.delete

@ActiveProfiles("test")
class AdminControllerTest : DefaultControllerTest() {

    @BeforeEach
    override fun setUp() {
        super.setUp()
    }

    @AfterEach
    override fun tearDown() {
        super.tearDown()
    }

    @Test
    fun givenUserUnAuthorized_WhenDeleteGuard_ThenReturns401() {
        val result = mockMvc.delete("/admin/guard/{guard_id}", 1).andReturn().response
        assertEquals(401, result.status)
    }

    @Test
    fun givenUserNotAdmin_WhenDeleteGuard_ThenReturns401() {
        val firebaseToken = Mockito.mock(FirebaseToken::class.java)
        val claim = mapOf("type" to "Member")

        Mockito.`when`(firebaseToken.claims).thenReturn(claim)
        Mockito.`when`(firebaseToken.uid).thenReturn(member.id)
        val idToken = "test_user_token"
        Mockito.`when`(auth.verifyIdToken(idToken, true)).thenReturn(firebaseToken)

        community.adminIds = setOf("some_id")
        member.community = community
        Mockito.`when`(mockUserService.getMemberById(member.id!!)).thenReturn(member)

        val result = mockMvc.delete("/admin/guard/{guard_id}", 1) {
            header("Authorization", "Bearer $idToken")
        }.andReturn().response
        assertEquals(401, result.status)
    }

    @Test
    fun handleCommunityJoinRequest() {
    }

    @Test
    fun addSecurityGuard() {
    }

    @Test
    fun removeSecurityGuard() {
    }

    @Test
    fun createLevy() {
    }

    @Test
    fun assignLevy() {
    }
}