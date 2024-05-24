package ng.cove.web.listener

import com.google.cloud.secretmanager.v1.SecretManagerServiceClient
import com.google.cloud.secretmanager.v1.SecretVersionName
import org.springframework.boot.context.event.ApplicationPreparedEvent
import org.springframework.context.ApplicationListener
import org.springframework.core.env.PropertiesPropertySource
import java.util.*

class StartupListener : ApplicationListener<ApplicationPreparedEvent> {
    override fun onApplicationEvent(event: ApplicationPreparedEvent) {
        val profile = event.applicationContext.environment.activeProfiles.getOrNull(0)
        // if empty, default to prod
        val isNotDev = profile.isNullOrBlank() || profile != "dev"

        // Get secrets from GCP, only run on test and prod
       if (isNotDev){
           val gcpProjectId = "gatedaccessdev"
           val firebaseSecret = SecretVersionName.of(gcpProjectId, "firebase-service-account", "1")
           val termiiSecret = SecretVersionName.of(gcpProjectId, "termii-key", "1")
           val dbSecret = SecretVersionName.of(gcpProjectId, "db-uri", "1")

           // Auto-closable
           SecretManagerServiceClient.create().use {
               val termiiSecretPayload =
                   it.accessSecretVersion(termiiSecret).payload.data.toByteArray().inputStream()
               val dbSecretPayload =
                   it.accessSecretVersion(dbSecret).payload.data.toByteArray().inputStream()
               val serviceAccountStream =
                   it.accessSecretVersion(firebaseSecret).payload.data.toByteArray().inputStream()

               val props = Properties()
               props["termii-key"] = String(termiiSecretPayload.readAllBytes())
               props["firebase-secret"] = String(serviceAccountStream.readAllBytes())
               props["spring.data.mongodb.uri"] = String(dbSecretPayload.readAllBytes())

               event.applicationContext.environment.propertySources
                   .addFirst(PropertiesPropertySource("d-secrets", props))
           }
       }
    }
}