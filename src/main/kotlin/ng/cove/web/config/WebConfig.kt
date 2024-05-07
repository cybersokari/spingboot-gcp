package ng.cove.web.config

import ng.cove.web.http.interceptor.SecureInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableWebMvc
class WebConfig(private val context: WebApplicationContext) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {

        registry.addInterceptor(SecureInterceptor(context))
            .addPathPatterns("/secure/**")
            .addPathPatterns("/admin/**")
    }
}
