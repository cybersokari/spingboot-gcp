package ng.cove.web.http.controller

import com.google.firebase.auth.FirebaseToken
import net.datafaker.Faker
import ng.cove.web.AppTest
import ng.cove.web.data.model.UserRole
import ng.cove.web.util.ApiResponseMessage.ENTER_AFTER_MUST_BE_BEFORE_EXIT_BEFORE
import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MemberControllerTest: AppTest() {

    private val idToken: String = faker.random().hex(30)
    private val firebaseToken: FirebaseToken = Mockito.mock(FirebaseToken::class.java)

    @BeforeEach
    fun authMember(){
        member = memberRepo.save(member)

        `when`(auth.verifyIdToken(idToken, true)).thenReturn(firebaseToken)
        `when`(firebaseToken.claims).thenReturn(mapOf("role" to UserRole.MEMBER.name))
        `when`(firebaseToken.uid).thenReturn(member.id)
    }

    @Test
    fun givenEnterTimeIsNotBeforeExitTime_whenUserBooks_thenReturn400(){
        //given
        val enterAfter = Date.from(Instant.now().plusSeconds(60))

        val body = mapOf(
            "name" to Faker().name().firstName(),
            "head_count" to 1,
            "enter_after" to enterAfter,
            "exit_before" to enterAfter
        )

        //when
        val result = mockMvc.perform(post("$API_VERSION/member/visitor/book")
            .contentType("application/json")
            .content(mapper.writeValueAsString(body))
            .header("Authorization", "Bearer $idToken")).andReturn()
        //then
        assert(result.response.status == 400)
        assert(result.response.contentAsString.contains(ENTER_AFTER_MUST_BE_BEFORE_EXIT_BEFORE))
    }

    @Test
    fun givenEnterTimeIsBeforeExitTime_whenUserBooks_thenReturn200(){
        //given
        val enterAfter = Date.from(Instant.now().plusSeconds(1))
        val exitBefore = Date.from(enterAfter.toInstant().plusSeconds(2000))
        val headCount = 1
        val body = mapOf(
            "name" to Faker().name().firstName(),
            "head_count" to headCount,
            "enter_after" to enterAfter,
            "exit_before" to exitBefore
        )

        //when
        val response = mockMvc.perform(post("$API_VERSION/member/visitor/book")
            .contentType("application/json")
            .content(mapper.writeValueAsString(body))
            .header("Authorization", "Bearer $idToken")).andReturn().response
        val booking = JSONObject(response.contentAsString)


        //then
        assert(response.status == 200)
        assertEquals(member.id, booking["host"])
        assertEquals(headCount, booking["head_count"] as Int)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        assertEquals(enterAfter, dateFormat.parse(booking["enter_after"] as String))
        assertEquals(exitBefore, dateFormat.parse(booking["exit_before"] as String))
    }
}