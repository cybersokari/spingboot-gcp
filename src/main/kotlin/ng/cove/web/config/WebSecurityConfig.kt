package ng.cove.web.config

import ng.cove.web.http.controller.API_VERSION
import ng.cove.web.http.filter.AuthRequestFilter
import ng.cove.web.http.filter.AdminFilter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.context.WebApplicationContext

val SECURE_PATHS = arrayOf(
    "$API_VERSION/admin/*", "$API_VERSION/member/*", "$API_VERSION/guard/*"
)

@Configuration
class WebSecurityConfig(val context: WebApplicationContext) {

    private var filterOrder = 0

    @Bean
    fun authFilter(): FilterRegistrationBean<AuthRequestFilter> {
        return FilterRegistrationBean<AuthRequestFilter>().apply {
            setFilter(AuthRequestFilter(context))
            addUrlPatterns(*SECURE_PATHS)
            order = filterOrder
        }
    }

    @Bean
    fun adminFilter(): FilterRegistrationBean<AdminFilter> {
        return FilterRegistrationBean<AdminFilter>().apply {
            setFilter(AdminFilter(context))
            addUrlPatterns("$API_VERSION/admin/*")
            order = filterOrder++
        }
    }
}