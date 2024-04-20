package co.gatedaccess.web

import com.google.firebase.FirebaseApp
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.ApplicationListener
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@SpringBootApplication
@EnableMongoRepositories
open class App{
    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
            val app = SpringApplication(App::class.java)
            app.addListeners(ApplicationListener { _: ApplicationStartedEvent? ->  /*init Firebase*/FirebaseApp.initializeApp() } as ApplicationListener<ApplicationStartedEvent?>)
            app.run(*args)
        }
    }

}
