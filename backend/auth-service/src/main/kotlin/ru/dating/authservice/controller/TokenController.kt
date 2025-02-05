package ru.dating.authservice.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.dating.authservice.dto.AuthResponseDTO
import ru.dating.authservice.enums.CookieName
import ru.dating.authservice.service.interfaces.TokenService

@Tag(
    name = "Токен",
    description = "Рефреш токена")
@RequestMapping("/api/auth/token")
@RestController
class TokenController(
    private val tokenService: TokenService
) {
    @Operation(
        summary = "Обновление токена",
        description = "Рефреш токенов пользователя, рефреш берется из cookie"
    )
    @PostMapping("/refresh")
    fun refreshToken(
        @CookieValue("REFRESH_TOKEN") refreshToken: String,
        response: HttpServletResponse
    ): ResponseEntity<AuthResponseDTO> {
        val newTokens = tokenService.refreshToken(refreshToken, response)
        return ResponseEntity.ok(newTokens)
    }
}