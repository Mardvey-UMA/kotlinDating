package ru.dating.authservice.service

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.stereotype.Service
import ru.dating.authservice.repository.TokenRepository

@Service
class LogoutService (
    private val tokenRepository: TokenRepository,
) : LogoutHandler {

    override fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication?
    ) {
        val authHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return
        }
        val jwt = authHeader.substring("Bearer ".length)
        val storedToken = tokenRepository.findByToken(jwt)

        storedToken?.let {
            it.expired = true
            it.revoked = true
            tokenRepository.save(it)
        }
        SecurityContextHolder.clearContext()

        val accessCookie = Cookie("accessToken", null).apply {
            maxAge = 0
            path = "/"
            isHttpOnly = true
        }
        val refreshCookie = Cookie("refreshToken", null).apply {
            maxAge = 0
            path = "/"
            isHttpOnly = true
        }
        response.addCookie(accessCookie)
        response.addCookie(refreshCookie)
    }

}