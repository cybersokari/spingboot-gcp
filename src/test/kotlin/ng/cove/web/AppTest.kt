package ng.cove.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.google.firebase.auth.FirebaseAuth
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import de.flapdoodle.embed.mongo.commands.ServerAddress
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.mongo.spring.autoconfigure.EmbeddedMongoAutoConfiguration
import de.flapdoodle.embed.mongo.transitions.Mongod
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess
import de.flapdoodle.reverse.TransitionWalker
import net.datafaker.Faker
import ng.cove.web.data.model.Community
import ng.cove.web.data.model.Member
import ng.cove.web.data.repo.CommunityRepo
import ng.cove.web.data.repo.JoinRequestRepo
import ng.cove.web.data.repo.MemberPhoneOtpRepo
import ng.cove.web.data.repo.MemberRepo
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.Mockito.mockStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Import
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import javax.annotation.PreDestroy


@SpringBootTest(classes = [App::class], properties = ["schedule.levy.duration.secs = 2"])
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource("/application–test.properties")
@Import(value = [EmbeddedMongoConfig::class])
@EnableAutoConfiguration(exclude = [EmbeddedMongoAutoConfiguration::class])
class AppTest {

    @Autowired
    lateinit var communityRepo: CommunityRepo

    @Autowired
    lateinit var memberRepo: MemberRepo

    @Autowired
    lateinit var joinRequestRepo: JoinRequestRepo

    @Autowired
    lateinit var memberPhoneOtpRepo: MemberPhoneOtpRepo

    @Autowired
    lateinit var mockMvc: MockMvc


    lateinit var staticFirebaseAuth: MockedStatic<FirebaseAuth>

    // Mocked FirebaseAuth for testing
    val auth: FirebaseAuth = Mockito.mock(FirebaseAuth::class.java)

    final val faker = Faker()
    val mapper = ObjectMapper().apply { propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE }

    lateinit var member: Member
    lateinit var community: Community

    @BeforeEach
    fun setUp() {

        community = Community()
        community.id = faker.random().hex(20)
        community.name = "${faker.address().state()} Community"
        community.address = faker.address().streetAddress()

        member = Member()
        member.id = faker.random().hex(20)
        member.firstName = faker.name().firstName()
        member.lastName = faker.name().lastName()
        member.phone = faker.phoneNumber().cellPhone()

        community.superAdminId = member.id
        community.adminIds = setOf(member.id!!)

        member.community = community

    }

    @BeforeAll
    fun setupAll() {
        // Mock FirebaseAuth
        staticFirebaseAuth = mockStatic(FirebaseAuth::class.java)
        staticFirebaseAuth.`when`<FirebaseAuth>(FirebaseAuth::getInstance).thenReturn(auth)
    }

    @AfterAll
    fun tearDownAll() {
        staticFirebaseAuth.close()
    }

}

@TestConfiguration
class EmbeddedMongoConfig : AbstractMongoClientConfiguration() {

    var embeddedMongo: TransitionWalker.ReachedState<RunningMongodProcess>? = null

    override fun getDatabaseName(): String = "test"

    override fun mongoClient(): MongoClient {
        // Embedded Mongo instance for testing
        embeddedMongo = Mongod.instance().start(Version.Main.V7_0)
        val serverAddress: ServerAddress = embeddedMongo!!.current().serverAddress
        val host = serverAddress.host
        val port = serverAddress.port
        return MongoClients.create("mongodb://$host:$port")
    }

    @PreDestroy
    fun onShutDown() {
        embeddedMongo?.close()
    }
}
