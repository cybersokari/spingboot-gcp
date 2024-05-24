package ng.cove.web.listener

import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient
import com.google.cloud.secretmanager.v1.SecretPayload
import com.google.cloud.secretmanager.v1.SecretVersionName
import com.google.protobuf.ByteString
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.Mockito.mockStatic
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.context.event.ApplicationPreparedEvent
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.Environment
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.test.assertEquals

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [SecretsSetupListener::class])
class SecretsSetupListenerTest {

    private lateinit var secretManagerStatic: MockedStatic<SecretManagerServiceClient>
    private val secretManagerClientMock = Mockito.mock(SecretManagerServiceClient::class.java)

    private lateinit var secretVersionNameStatic: MockedStatic<SecretVersionName>
    private val secretVersionNameMock = Mockito.mock(SecretVersionName::class.java)

    private val secretResponseMock = Mockito.mock(AccessSecretVersionResponse::class.java)
    private val secretPayload = Mockito.mock(SecretPayload::class.java)

    @Autowired
    lateinit var configurableAppContext: ConfigurableApplicationContext

    @Autowired
    lateinit var environment: Environment

    @BeforeEach
    fun setUp() {
        secretVersionNameStatic = mockStatic(SecretVersionName::class.java).apply {
            `when`<SecretVersionName> { SecretVersionName.of(any(), any(), any()) }.thenReturn(secretVersionNameMock)
        }

        secretManagerStatic = mockStatic(SecretManagerServiceClient::class.java).apply {
            `when`<Any>(SecretManagerServiceClient::create).thenReturn(secretManagerClientMock)
        }
    }

    @AfterEach
    fun tearDown() {
        secretVersionNameStatic.close()
        secretManagerStatic.close()
    }

    @Test
    fun givenOnApplicationPreparedEvent_whenSecretsAreRetrieved_thenSecretsAreAvailableInProperties() {

        val secret = "test-secret"
        Mockito.`when`(secretManagerClientMock.accessSecretVersion(any<SecretVersionName>()))
            .thenReturn(secretResponseMock)
        Mockito.`when`(secretResponseMock.payload).thenReturn(secretPayload)
        Mockito.`when`(secretPayload.data).thenReturn(ByteString.copyFrom(secret, Charsets.UTF_8))

        val appPreEvent = ApplicationPreparedEvent(SpringApplication(), null, configurableAppContext)
        SecretsSetupListener().onApplicationEvent(appPreEvent)

        verify(secretManagerClientMock, times(3)).accessSecretVersion(any<SecretVersionName>())
        verify(secretResponseMock, times(3)).payload
        verify(secretPayload, times(3)).data
        assertEquals(secret, environment.getProperty("termii-key"))
        assertEquals(secret, environment.getProperty("firebase-secret"))
        assertEquals(secret, environment.getProperty("spring.data.mongodb.uri"))
    }
}