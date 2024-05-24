package ng.cove.web.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("!test")
@Configuration
class FirebaseConfig(val context: ApplicationContext) {

    @Bean
    fun initApp(): FirebaseApp {
        val secret = context.environment.getProperty("firebase-secret")
        return if (secret != null) {
            val stream = secret.toByteArray().inputStream()
            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(stream))
                .build()
            FirebaseApp.initializeApp(options)
        } else {
            FirebaseApp.initializeApp()
        }
    }
}