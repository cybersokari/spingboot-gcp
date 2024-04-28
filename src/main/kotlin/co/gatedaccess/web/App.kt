package co.gatedaccess.web

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.ApplicationListener
import org.springframework.core.io.ClassPathResource
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import java.io.FileInputStream


@SpringBootApplication
@EnableMongoRepositories("co.gatedaccess.web.data.repo")
class App {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val app = SpringApplication(App::class.java)
            val ls = ApplicationListener<ApplicationStartedEvent> {

                val serviceAccount =
                    FileInputStream(ClassPathResource("service-account.json").file)
                val options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build()
                FirebaseApp.initializeApp(options)
            }
            app.addListeners(ls)
            app.run(*args)
        }
    }

}
