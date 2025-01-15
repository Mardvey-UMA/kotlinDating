package ru.dating.authservice.controller

import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.dating.authservice.dto.AuthResponseDTO
import ru.dating.authservice.service.impl.AuthenticationServiceImpl
@Tag(name = "User6")
@RequestMapping("/api/token")
@RestController
class TokenController(
    private val authenticationService: AuthenticationServiceImpl
) {
    @PostMapping("/refresh")
    fun refreshToken(
        @CookieValue("refreshToken") refreshToken: String,
        response: HttpServletResponse
    ): ResponseEntity<AuthResponseDTO> {
        val newTokens = authenticationService.refreshToken(refreshToken, response)
        return ResponseEntity.ok(newTokens)
    }
}