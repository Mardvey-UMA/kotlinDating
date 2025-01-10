package ru.dating.authservice.service

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.stereotype.Service
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
        val refreshToken = getCookieValue(request, REFRESH_TOKEN_COOKIE_NAME)

        if (refreshToken != null) {
            val storedToken = tokenRepository.findByToken(refreshToken)
            if (storedToken != null && !storedToken.revoked && !storedToken.expired) {
                storedToken.revoked = true
                storedToken.expired = true
                tokenRepository.save(storedToken)
            }
        }

        SecurityContextHolder.clearContext()

        clearCookie(response, REFRESH_TOKEN_COOKIE_NAME)

        clearCookie(response, ACCESS_TOKEN_COOKIE_NAME)
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
    /*
    TODO(Вынести в ENUM названия
     */
    companion object {
        const val REFRESH_TOKEN_COOKIE_NAME = "refreshToken"
        const val ACCESS_TOKEN_COOKIE_NAME = "accessToken"
    }
}
