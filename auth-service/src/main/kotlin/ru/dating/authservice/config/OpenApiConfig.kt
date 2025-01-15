package ru.dating.authservice.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.util.UriComponentsBuilder

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .servers(listOf(Server().url("http://localhost:80")))
            .info(
                Info()
                    .title("Auth Service API")
                    .description("API for hadle register/login/oauth2")
                    .version("v1.0")
            )
    }
}
