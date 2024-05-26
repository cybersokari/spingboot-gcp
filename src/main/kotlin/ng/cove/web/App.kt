package ng.cove.web

import com.github.benmanes.caffeine.cache.Caffeine
import ng.cove.web.listener.SecretsSetupListener
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.client.RestTemplate
import java.util.concurrent.TimeUnit


@EnableAsync
@EnableCaching
@EnableScheduling
@EnableMongoRepositories("ng.cove.web.data.repo")
@SpringBootApplication
class App {

    @Bean
    fun restTemplate() = RestTemplate()

    @Bean
    fun caffeineConfig(): Caffeine<Any, Any> {
        return Caffeine.newBuilder().expireAfterWrite(60, TimeUnit.MINUTES)
    }

    @Bean
    fun cacheManager(caffeine: Caffeine<Any, Any>): CaffeineCacheManager {
        val cacheManager = CaffeineCacheManager()
        cacheManager.setCaffeine(caffeine)
        return cacheManager
    }
}

fun main(args: Array<String>) {
    SpringApplication(App::class.java).apply {
        addListeners(SecretsSetupListener())
    }.run(*args)
}