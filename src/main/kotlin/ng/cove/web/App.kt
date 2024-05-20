package ng.cove.web

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient
import com.google.cloud.secretmanager.v1.SecretVersionName
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import de.flapdoodle.embed.mongo.spring.autoconfigure.EmbeddedMongoAutoConfiguration
import ng.cove.web.component.SmsOtpService
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import java.util.concurrent.TimeUnit

@EnableAsync
@EnableCaching
@SpringBootApplication(exclude = [MongoDataAutoConfiguration::class, EmbeddedMongoAutoConfiguration::class])
class App {

    @Bean
    fun caffeineConfig(): Caffeine<Any, Any> {
        return Caffeine.newBuilder().expireAfterWrite(60, TimeUnit.MINUTES)
    }

    @Bean
    fun cacheManager(caffeine: Caffeine<Any, Any>): CaffeineCacheManager {
        val cacheManager = CaffeineCacheManager()
        cacheManager.setCaffeine(caffeine)
        return cacheManager
    }
}

fun main(args: Array<String>) {
    val app = SpringApplication(App::class.java)

    val startedEvent = ApplicationListener<ApplicationStartedEvent> { event ->

        val profiles = event.applicationContext.environment.activeProfiles
        // profiles is sometimes empty when running on a production server because it
        // has not been loaded from the application.properties file at this point
        val profile = profiles.getOrNull(0) ?: "prod"

        when (profile) {
            "test" -> {}
            "dev" -> FirebaseApp.initializeApp()
            "prod" -> {
                // Get secrets from GCP
                val gcpProjectId = "gatedaccessdev"
                val firebaseSecret = SecretVersionName.of(gcpProjectId, "firebase-service-account", "1")
                val termiiSecret = SecretVersionName.of(gcpProjectId, "termii-key", "1")

                // Auto-closable
                SecretManagerServiceClient.create().use {
                    // Set Termii API Key to service
                    val termiiSecretPayload =
                        it.accessSecretVersion(termiiSecret).payload.data.toByteArray().inputStream()
                    val smsOtpService = event.applicationContext.getBean(SmsOtpService::class.java)
                    smsOtpService.termiiApiKey = String(termiiSecretPayload.readAllBytes())
                    // Init Firebase with service account
                    val serviceAccountStream =
                        it.accessSecretVersion(firebaseSecret).payload.data.toByteArray().inputStream()
                    val options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                        .build()
                    FirebaseApp.initializeApp(options)
                }
            }
        }

    }

    app.addListeners(startedEvent)
    app.run(*args)
}