package ru.dating.authservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableAsync
import ru.dating.authservice.config.EmailConfig
import ru.dating.authservice.config.JwtConfig

@EnableJpaAuditing
@EnableAsync
@SpringBootApplication
@EnableConfigurationProperties(JwtConfig::class, EmailConfig::class)
class AuthServiceApplication

fun main(args: Array<String>) {
    runApplication<AuthServiceApplication>(*args)
}
