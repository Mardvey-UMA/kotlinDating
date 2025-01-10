package ru.dating.authservice.service.impl

import org.springframework.stereotype.Service
import ru.dating.authservice.entity.Token
import ru.dating.authservice.entity.User
import ru.dating.authservice.enums.TokenType
import ru.dating.authservice.repository.TokenRepository
import ru.dating.authservice.service.interfaces.TokenService
import java.time.LocalDateTime

@Service
class TokenServiceImpl(
    private val tokenRepository: TokenRepository
) : TokenService {

    override fun saveRefreshToken(user: User, refreshToken: String) {
        val token = Token(
            user = user,
            token = refreshToken,
            tokenType = TokenType.REFRESH,
            expired = false,
            revoked = false,
            created = LocalDateTime.now()
        )
        tokenRepository.save(token)
    }

    override fun revokeRefreshToken(token: String) {
        val storedToken = tokenRepository.findByToken(token)
        storedToken?.let {
            it.revoked = true
            it.expired = true
            tokenRepository.save(it)
        }
    }

    override fun findRefreshToken(token: String): Token? = tokenRepository.findByToken(token)

    override fun revokeAllRefreshTokens(user: User) {
        val tokens = tokenRepository.findValidRefreshTokensByUser(user.id!!)
        tokens.forEach {
            it.revoked = true
            it.expired = true
        }
        tokenRepository.saveAll(tokens)
    }
}
