package ru.dating.authservice.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "spring")
class PublicRoutesConfig {
    lateinit var publicUrls: List<String>
}