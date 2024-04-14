package co.gatedaccess.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import java.util.Date;

@Configuration
@EnableMongoAuditing(auditorAwareRef = "auditorProvider")
public class MongoDbConfig {
    @Bean
    public AuditorAware<Date> auditorProvider() {
        return new AuditorAwareImpl();
    }
}
