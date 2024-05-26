package ng.cove.web.config

import ng.cove.web.http.interceptor.AdminInterceptor
import ng.cove.web.http.interceptor.SecureInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(val context: WebApplicationContext) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {

        registry.addInterceptor(SecureInterceptor(context))
            .addPathPatterns("/member/**")
            .addPathPatterns("/admin/**")
            .addPathPatterns("/guard/**")

        registry.addInterceptor(AdminInterceptor(context))
            .addPathPatterns("/admin/**").order(1)
    }
}
