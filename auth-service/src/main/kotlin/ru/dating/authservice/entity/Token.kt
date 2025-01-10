package ru.dating.authservice.entity

import jakarta.persistence.*
import ru.dating.authservice.enums.TokenType

@Entity
class Token (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id : Long,

    @Column(unique = true)
    private var token: String,

    @Enumerated(EnumType.STRING)
    var tokenType: TokenType = TokenType.BEARER,

    var revoked: Boolean,

    var expired: Boolean,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User
    )

