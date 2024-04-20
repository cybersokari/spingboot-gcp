package co.gatedaccess.web.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.mongodb.config.EnableMongoAuditing
import java.util.*

@Configuration
@EnableMongoAuditing(auditorAwareRef = "auditorProvider")
open class MongoDbConfig {
    @Bean
    open fun auditorProvider(): AuditorAware<Date> {
        return AuditorAwareImpl()
    }
}
