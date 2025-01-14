package ru.dating.authservice.config
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "spring.security.oauth2.client")
class VkApiConfig {
    lateinit var registration: Registration
    lateinit var provider: Provider

    data class Registration(
        var vk: ClientRegistration
    )

    data class Provider(
        var vk: ClientProvider
    )

    data class ClientRegistration(
        var clientName: String = "",
        var clientId: String = "",
        var clientSecret: String = "",
        var redirectUri: String = "",
        var authorizationGrantType: String = "",
        var clientAuthenticationMethod: String = "",
        var scope: List<String> = listOf(),
        var provider: String = ""
    )

    data class ClientProvider(
        var authorizationUri: String = "",
        var tokenUri: String = "",
        var userInfoUri: String = "",
        var userNameAttribute: String = ""
    )
}
