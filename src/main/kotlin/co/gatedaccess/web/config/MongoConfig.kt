package co.gatedaccess.web.config

import com.google.cloud.secretmanager.v1.SecretManagerServiceClient
import com.google.cloud.secretmanager.v1.SecretVersionName
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.StandardEnvironment
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration

@Configuration
class MongoConfig : AbstractMongoClientConfiguration() {
    override fun getDatabaseName(): String {
        return "dev"
    }

    override fun mongoClient(): MongoClient {
        val profiles = StandardEnvironment().activeProfiles
        // profiles is usually empty at this point, when running in production
        val runningOnProd = profiles.isEmpty() || profiles[0] == "prod"

        if (runningOnProd){
            //Auto-closable client
            SecretManagerServiceClient.create().use {
                val gcpProjectId = "gatedaccessdev"
                val dbSecret = SecretVersionName.of(gcpProjectId, "db-uri", "1")
                // Access the secret version.
                val dbSecretPayload = it.accessSecretVersion(dbSecret).payload.data.toByteArray().inputStream()
                //Close the client
                return MongoClients.create(String(dbSecretPayload.readAllBytes()))
            }
        }else{
            return MongoClients.create("mongodb://localhost:27017/dev")
        }

    }

}