package ru.dating.authservice.config

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "spring.security.jwt")
class JwtConfig {
    lateinit var secretKey: String
    var expiration: Long = 0
    var refreshExpiration: Long = 0
}
