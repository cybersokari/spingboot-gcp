package ng.cove.web.config

import com.google.cloud.secretmanager.v1.SecretManagerServiceClient
import com.google.cloud.secretmanager.v1.SecretVersionName
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
import org.springframework.web.context.WebApplicationContext

@Profile("!test")
@Configuration
class MongoConfig(val context: WebApplicationContext) : AbstractMongoClientConfiguration() {

    override fun getDatabaseName(): String = "dev"

    override fun mongoClient(): MongoClient {
        val profiles = context.environment.activeProfiles
        // profiles is sometimes empty when running on a production server because it
        // has not been loaded from the application.properties file at this point
        val profile = profiles.getOrNull(0) ?: "prod"

        when (profile) {
            "dev" -> return MongoClients.create("mongodb://localhost:27017/dev")
            "prod" -> {
                //Auto-closable client
                SecretManagerServiceClient.create().use {
                    val gcpProjectId = "gatedaccessdev"
                    val dbSecret = SecretVersionName.of(gcpProjectId, "db-uri", "1")
                    // Access the secret version.
                    val dbSecretPayload = it.accessSecretVersion(dbSecret).payload.data.toByteArray().inputStream()
                    //Close the client
                    return MongoClients.create(String(dbSecretPayload.readAllBytes()))
                }
            }

            else -> throw Exception("This profile has no db configuration")
        }

    }
    override fun autoIndexCreation(): Boolean = true
}