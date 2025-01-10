package ru.dating.authservice.entity

import jakarta.persistence.*
import ru.dating.authservice.enums.MailTokenType
import java.time.LocalDateTime

@Entity
class MailToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var token: String,
    @Enumerated(EnumType.STRING)
    var tokenType: MailTokenType,
    var expiresAt: LocalDateTime,
    var createdAt: LocalDateTime,
    var validatedAt: LocalDateTime? = null,
    var enabled: Boolean = true,
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false) var user: User
)