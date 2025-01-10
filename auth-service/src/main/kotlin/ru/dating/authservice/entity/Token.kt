package ru.dating.authservice.entity

import jakarta.persistence.*
import ru.dating.authservice.enums.TokenType
import java.time.LocalDateTime

@Entity
class Token (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id : Long? = null,

    @Column(unique = true)
    private var token: String,

    @Enumerated(EnumType.STRING)
    var tokenType: TokenType = TokenType.REFRESH,

    var revoked: Boolean,

    var expired: Boolean,

    var created: LocalDateTime = LocalDateTime.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User
    )

