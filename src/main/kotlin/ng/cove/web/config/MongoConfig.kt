package ng.cove.web.config

import com.google.cloud.secretmanager.v1.SecretManagerServiceClient
import com.google.cloud.secretmanager.v1.SecretVersionName
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import de.flapdoodle.embed.mongo.commands.ServerAddress
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.mongo.transitions.Mongod
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess
import de.flapdoodle.reverse.TransitionWalker
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.web.context.WebApplicationContext
import javax.annotation.PreDestroy

//@Profile("!test")
@Configuration
@EnableMongoRepositories("ng.cove.web.data.repo")
class MongoConfig(val context: WebApplicationContext) : AbstractMongoClientConfiguration() {

    var embeddedMongo: TransitionWalker.ReachedState<RunningMongodProcess>? = null

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
            else -> {
                // Embedded Mongo instance for testing
                embeddedMongo = Mongod.instance().start(Version.Main.V7_0)
                val serverAddress: ServerAddress = embeddedMongo!!.current().serverAddress
                val host = serverAddress.host
                val port = serverAddress.port
                return MongoClients.create("mongodb://$host:$port/test")
            }
        }

    }

    @PreDestroy
    fun onShutDown() {
        embeddedMongo?.close()
    }

    override fun autoIndexCreation(): Boolean = true

}