package ng.cove.web.http.filter

import com.google.firebase.auth.FirebaseToken
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.datafaker.Faker
import ng.cove.web.AppTest
import ng.cove.web.data.model.Admin
import ng.cove.web.data.model.Member
import ng.cove.web.data.model.SecurityGuard
import ng.cove.web.data.model.UserType
import ng.cove.web.data.repo.AdminRepo
import ng.cove.web.data.repo.SecurityGuardRepo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.context.WebApplicationContext
import java.io.PrintWriter


class AuthRequestFilterTest : AppTest() {

    @Autowired
    lateinit var adminRepo: AdminRepo

    @Autowired
    lateinit var guardRepo: SecurityGuardRepo

    @Mock
    lateinit var httpRequest: HttpServletRequest

    @Mock
    lateinit var httpResponse: HttpServletResponse

    @Mock
    lateinit var filterChain: FilterChain

    @Mock
    lateinit var writer: PrintWriter

    @Autowired
    lateinit var context: WebApplicationContext

    private val idToken: String = Faker().random().hex(30)
    private val firebaseToken: FirebaseToken = Mockito.mock(FirebaseToken::class.java)

    @BeforeEach
    fun setupAuth() {
        `when`(auth.verifyIdToken(idToken, true)).thenReturn(firebaseToken)
        `when`(httpResponse.writer).thenReturn(writer)
        doNothing().`when`(writer).write(any<String>())
    }

    @AfterEach
    fun reset() {
        reset(httpRequest)
        reset(httpResponse)
        reset(filterChain)
    }


    @Test
    fun givenInvalidUser_whenOnHandleRequest_thenReturnFalse() {
        // Given
        val invalidIdToken = "23456789"
        `when`(httpRequest.getHeader("Authorization")).thenReturn("Bearer $invalidIdToken")
        val filter = AuthRequestFilter(context)
        // When

        filter.doFilterInternal(httpRequest, httpResponse, filterChain)

        //Then
        verify(filterChain, never()).doFilter(httpRequest, httpResponse)
        verify(httpRequest, never()).setAttribute(any(), any())
        verify(httpResponse, times(1)).status = 401
    }

    @Nested
    inner class MemberUserTest {
        @BeforeEach
        fun authMember() {
            member = memberRepo.save(member)
            `when`(firebaseToken.claims).thenReturn(mapOf("type" to UserType.MEMBER.name))
            `when`(firebaseToken.uid).thenReturn(member.id)
        }

        @Test
        fun givenValidMember_whenOnHandleRequest_thenReturnTrue() {
            // Given
            `when`(httpRequest.getHeader("Authorization")).thenReturn("Bearer $idToken")
            val filter = AuthRequestFilter(context)
            // When
            filter.doFilterInternal(httpRequest, httpResponse, filterChain)

            //Then
            verify(filterChain, times(1)).doFilter(httpRequest, httpResponse)
            verify(httpRequest, times(1)).setAttribute(any<String>(), any<Member>())
            verifyNoInteractions(httpResponse)
        }

    }

    @Nested
    inner class AdminUserTest {

        private lateinit var admin: Admin

        @BeforeEach
        fun authAdmin() {
            admin = Admin().apply {
                firstName = member.firstName
                lastName = member.lastName
                phone = member.phone
                communityId = member.communityId
            }
            admin = adminRepo.save(admin)
            `when`(firebaseToken.claims).thenReturn(mapOf("type" to UserType.ADMIN.name))
            `when`(firebaseToken.uid).thenReturn(admin.id)

        }

        @Test
        fun givenValidAdmin_whenOnHandleRequest_thenReturnTrue() {
            // Given
            `when`(httpRequest.getHeader("Authorization")).thenReturn("Bearer $idToken")
            val filter = AuthRequestFilter(context)
            // When
            filter.doFilterInternal(httpRequest, httpResponse, filterChain)

            //Then
            verify(filterChain, times(1)).doFilter(httpRequest, httpResponse)
            verify(httpRequest, times(1)).setAttribute(any(), any<Admin>())
            verifyNoInteractions(httpResponse)
        }
    }

    @Nested
    inner class GuardUserTest {

        private lateinit var guard: SecurityGuard

        @BeforeEach
        fun authSecurityGuard() {
            guard = SecurityGuard().apply {
                firstName = member.firstName
                lastName = member.lastName
                phone = member.phone
                communityId = member.communityId
            }
            guard = guardRepo.save(guard)
            `when`(firebaseToken.claims).thenReturn(mapOf("type" to UserType.GUARD.name))
            `when`(firebaseToken.uid).thenReturn(guard.id)

        }

        @Test
        fun givenValidGuard_whenOnHandleRequest_thenReturnTrue() {
            // Given
            `when`(httpRequest.getHeader("Authorization")).thenReturn("Bearer $idToken")
            val filter = AuthRequestFilter(context)
            // When
            filter.doFilterInternal(httpRequest, httpResponse, filterChain)

            //Then
            verify(filterChain, times(1)).doFilter(httpRequest, httpResponse)
            verify(httpRequest, times(1)).setAttribute(any(), any<SecurityGuard>())
            verifyNoInteractions(httpResponse)
        }
    }

}