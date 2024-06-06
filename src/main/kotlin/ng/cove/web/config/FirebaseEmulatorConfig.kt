package ng.cove.web.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("dev")
@Configuration
class FirebaseEmulatorConfig {

    @Bean(destroyMethod = "destroy", initMethod = "init")
    fun setUp(): FirebaseEmulatorBean = FirebaseEmulatorBean()

}

class FirebaseEmulatorBean {
    private var process: Process? = null
    fun init() {
        process = ProcessBuilder(
            "bash",
            "-c",
            "firebase emulators:start --only auth"
        ).start()
    }

    fun destroy() = process?.destroy()
}