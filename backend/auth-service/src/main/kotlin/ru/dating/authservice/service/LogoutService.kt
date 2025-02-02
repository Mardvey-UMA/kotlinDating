package ru.dating.authservice.service

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.stereotype.Service
import ru.dating.authservice.enums.CookieName
import ru.dating.authservice.repository.TokenRepository

@Service
class LogoutService(
    private val tokenRepository: TokenRepository
) : LogoutHandler {

    override fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication?
    ) {
        val refreshToken = getCookieValue(request, CookieName.REFRESH_TOKEN.name)

        if (refreshToken != null) {
            val storedToken = tokenRepository.findByToken(refreshToken)
            if (storedToken != null && !storedToken.revoked && !storedToken.expired) {
                storedToken.revoked = true
                storedToken.expired = true
                tokenRepository.save(storedToken)
            }
        }

        SecurityContextHolder.clearContext()

        clearCookie(response, CookieName.REFRESH_TOKEN.name)

        clearCookie(response, CookieName.ACCESS_TOKEN.name)
    }

    private fun getCookieValue(request: HttpServletRequest, name: String): String? {
        return request.cookies?.find { it.name == name }?.value
    }

    private fun clearCookie(response: HttpServletResponse, name: String) {
        val cookie = Cookie(name, null).apply {
            maxAge = 0
            path = "/"
            isHttpOnly = true
        }
        response.addCookie(cookie)
    }
}
