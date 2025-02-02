package ru.dating.authservice.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "mailing")
class EmailConfig {
    lateinit var activationUrl: String
    lateinit var emailAddressSender: String
    var activationTokenExpiration: Long = 60
}