package ru.dating.authservice

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableAsync
import ru.dating.authservice.config.EmailConfig
import ru.dating.authservice.config.JwtConfig
import ru.dating.authservice.entity.Role
import ru.dating.authservice.repository.RoleRepository

@EnableJpaAuditing
@EnableAsync
@SpringBootApplication
@EnableConfigurationProperties(JwtConfig::class, EmailConfig::class)
class AuthServiceApplication{
    @Bean
    fun runner(roleRepository: RoleRepository): CommandLineRunner {
        return CommandLineRunner {
            if (roleRepository.findByName("USER") == null) {
                roleRepository.save(Role(name = "USER"))
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<AuthServiceApplication>(*args)
}
