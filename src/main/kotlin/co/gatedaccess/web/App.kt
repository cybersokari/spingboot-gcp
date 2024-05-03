package co.gatedaccess.web

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient
import com.google.cloud.secretmanager.v1.SecretVersionName
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.ApplicationListener
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.StandardEnvironment
import org.springframework.core.io.ClassPathResource
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import java.io.InputStream
import java.util.*


@SpringBootApplication(exclude = [MongoDataAutoConfiguration::class])
@EnableMongoRepositories("co.gatedaccess.web.data.repo")
class App {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val app = SpringApplication(App::class.java)

            val startedEvent = ApplicationListener<ApplicationStartedEvent> {

                val profiles = StandardEnvironment().activeProfiles
                val profile = if (profiles.isNotEmpty()) {
                    profiles[0]
                } else {
                    "prod"
                }

                val serviceAccountStream: InputStream
                if (profile == "dev") {
                    serviceAccountStream = ClassPathResource("service-account.json").inputStream
                } else {

                    // Get secrets from GCP
                    val gcpProjectId = "gatedaccessdev"
                    val firebaseSecret = SecretVersionName.of(gcpProjectId, "firebase-service-account", "1")
                    val termiiSecret = SecretVersionName.of(gcpProjectId, "termii-key", "1")

                    // Auto-closable
                    SecretManagerServiceClient.create().use {
                        serviceAccountStream =
                            it.accessSecretVersion(firebaseSecret).payload.data.toByteArray().inputStream()
                        val termiiSecretPayload =
                            it.accessSecretVersion(termiiSecret).payload.data.toByteArray().inputStream()

                        val environment: ConfigurableEnvironment = StandardEnvironment()
                        val propertySources = environment.propertySources
                        val myMap: MutableMap<String, Any> = HashMap()
                        myMap["termii.api.key"] = String(termiiSecretPayload.readAllBytes())
                        propertySources.addFirst(MapPropertySource("MY_MAP", myMap))
                        // Set Secret to application properties
//                        val props = Properties()
//                        println("Termii Secret: ${String(termiiSecretPayload.readAllBytes())}")
//                        props.setProperty("termii.api.key", String(termiiSecretPayload.readAllBytes()))
//                        app.setDefaultProperties(props)
                    }

                }
                // Init Firebase only when app is started
                val options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                    .build()
                FirebaseApp.initializeApp(options)
            }

            app.addListeners(startedEvent)
            app.run(*args)
        }
    }

}
