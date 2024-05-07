package ng.cove.web

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient
import com.google.cloud.secretmanager.v1.SecretVersionName
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import ng.cove.web.component.SmsOtpService
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.ApplicationListener
import org.springframework.core.env.StandardEnvironment
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.scheduling.annotation.EnableAsync

@EnableAsync
@SpringBootApplication(exclude = [MongoDataAutoConfiguration::class])
@EnableMongoRepositories("ng.cove.web.data.repo")
class App

fun main(args: Array<String>) {
    val app = SpringApplication(ng.cove.web.App::class.java)

    val startedEvent = ApplicationListener<ApplicationStartedEvent> { event ->

        val profiles = StandardEnvironment().activeProfiles
        // profiles is sometimes empty when running on a production server because it
        // has not been loaded from the application.properties file at this point
        val runningOnProd = profiles.isEmpty() || profiles[0] == "prod"

        if (!runningOnProd) {
            // Init Firebase with Application default credentials from Gcloud or Firebase CLI
            FirebaseApp.initializeApp()
        } else {

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

    app.addListeners(startedEvent)
    app.run(*args)
}