package ng.cove.web.listener

import com.google.cloud.secretmanager.v1.SecretManagerServiceClient
import com.google.cloud.secretmanager.v1.SecretVersionName
import org.springframework.boot.context.event.ApplicationPreparedEvent
import org.springframework.context.ApplicationListener
import org.springframework.core.env.PropertiesPropertySource
import java.util.*

class SecretsSetupListener : ApplicationListener<ApplicationPreparedEvent> {
    override fun onApplicationEvent(event: ApplicationPreparedEvent) {
        val context = event.applicationContext
        val profile = context.environment.activeProfiles.getOrNull(0)
        // if empty, default to prod
        val isNotDev = profile.isNullOrBlank() || profile != "dev"

        // Get secrets from GCP, only run on test and prod
       if (isNotDev){
           val gcpProjectId = context.environment.getProperty("secretmanager-project-id")!!
           val firebaseSecret = SecretVersionName.of(gcpProjectId, "firebase-service-account", "1")
           val termiiSecret = SecretVersionName.of(gcpProjectId, "termii-key", "1")
           val dbSecret = SecretVersionName.of(gcpProjectId, "db-uri", "1")

           // Auto-closable
           SecretManagerServiceClient.create().use {
               val termiiSecretPayload =
                   it.accessSecretVersion(termiiSecret).payload.data.toByteArray().inputStream()
               val dbSecretPayload =
                   it.accessSecretVersion(dbSecret).payload.data.toByteArray().inputStream()
               val serviceAccountPayload =
                   it.accessSecretVersion(firebaseSecret).payload.data.toByteArray().inputStream()

               val props = Properties()
               props["termii-key"] = String(termiiSecretPayload.readAllBytes())
               props["firebase-secret"] = String(serviceAccountPayload.readAllBytes())
               props["spring.data.mongodb.uri"] = String(dbSecretPayload.readAllBytes())

               context.environment.propertySources
                   .addFirst(PropertiesPropertySource("d-secrets", props))
           }
       }
    }
}