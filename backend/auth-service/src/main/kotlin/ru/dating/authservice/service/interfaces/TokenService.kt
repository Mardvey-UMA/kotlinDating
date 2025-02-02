package ru.dating.authservice.service.interfaces

import jakarta.servlet.http.HttpServletResponse
import ru.dating.authservice.dto.AuthResponseDTO
import ru.dating.authservice.entity.Token
import ru.dating.authservice.entity.User

interface TokenService {
    fun saveRefreshToken(user: User, refreshToken: String)
    fun revokeRefreshToken(token: String)
    fun findRefreshToken(token: String): Token?
    fun revokeAllRefreshTokens(user: User)
    fun refreshToken(refreshToken: String, response: HttpServletResponse): AuthResponseDTO
}
