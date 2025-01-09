package ru.dating.authservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.dating.authservice.entity.MailToken

interface MailTokenRepository: JpaRepository<MailToken, Long> {
    fun findByToken(token: String): MailToken?
}