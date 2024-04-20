package co.gatedaccess.web.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SwaggerConfig {
    private var schemeName: String = "bearerAuth"
    private var bearerFormat: String = "JWT"
    private var scheme: String = "bearer"

    @Bean
    open fun caseOpenAPI(): OpenAPI {
        return OpenAPI()
            .addSecurityItem(
                SecurityRequirement()
                    .addList(schemeName)
            ).components(
                Components()
                    .addSecuritySchemes(
                        schemeName, SecurityScheme()
                            .name(schemeName)
                            .type(SecurityScheme.Type.HTTP)
                            .bearerFormat(bearerFormat)
                            .`in`(SecurityScheme.In.HEADER)
                            .scheme(scheme)
                    )
            )
            .info(
                Info()
                    .title("gatedaccess.co web service")
                    .description("API documentation for GatedAccess Platform")
                    .version("1.0")
            )
    }
}
