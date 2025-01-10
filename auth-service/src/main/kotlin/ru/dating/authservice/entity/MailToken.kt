package ru.dating.authservice.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import ru.dating.authservice.enums.MailTokenType
import java.time.LocalDateTime

@Entity
class MailToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var token: String,
    var type: MailTokenType,
    var expiresAt: LocalDateTime,
    var createdAt: LocalDateTime,
    var validatedAt: LocalDateTime? = null,
    var enabled: Boolean = true,
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false) var user: User
)