package ng.cove.web

import ng.cove.web.data.repo.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class AppTests {

    @MockBean
    lateinit var communityRepo: CommunityRepo

    @MockBean
    lateinit var memberRepo: MemberRepo

    @MockBean
    lateinit var securityGuardRepo: SecurityGuardRepo

    @MockBean
    lateinit var joinRequestRepo: JoinRequestRepo

    @MockBean
    lateinit var accessRepo: AccessRepo

    @MockBean
    lateinit var assignedLevyRepo: AssignedLevyRepo

    @MockBean
    lateinit var levyPaymentRepo: LevyPaymentRepo

    @MockBean
    lateinit var memberPhoneOtpRepo: MemberPhoneOtpRepo

    @MockBean
    lateinit var occupantRepo: OccupantRepo

    @MockBean
    lateinit var levyRepo: LevyRepo

}
