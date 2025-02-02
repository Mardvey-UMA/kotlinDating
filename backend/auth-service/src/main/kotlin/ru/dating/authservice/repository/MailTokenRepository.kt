package ru.dating.authservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.dating.authservice.entity.MailToken
import ru.dating.authservice.enums.MailTokenType

interface MailTokenRepository: JpaRepository<MailToken, Long> {
    fun findByToken(token: String): MailToken?
    fun findByUserId(userId: Long): List<MailToken>
    fun findByUserIdAndEnabledAndTokenType(
        userId: Long,
        enabled: Boolean,
        tokenType: MailTokenType
    ): List<MailToken>
}