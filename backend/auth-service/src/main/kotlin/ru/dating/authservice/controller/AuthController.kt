package ru.dating.authservice.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import ru.dating.authservice.dto.*
import ru.dating.authservice.service.interfaces.AuthenticationService
import ru.dating.authservice.service.LogoutService
import ru.dating.authservice.service.interfaces.PasswordRecoveryService
import ru.dating.authservice.service.interfaces.RegistrationService
@Tag(
    name = "Вход и Регистрация",
    description = "Обработка входа и регистрации пользователя")
@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val registrationService: RegistrationService,
    private val authenticationService: AuthenticationService
) {

    @Operation(
        summary = "Регистрация",
        description = "Регистрация пользователя по почте юзернейму и паролю"
    )
    @PostMapping("/register")
    fun registerUser(@RequestBody @Valid registrationRequest: UserRequestDTO): ResponseEntity<UserResponseDTO> {
        val response = registrationService.register(registrationRequest)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @Operation(
        summary = "Авторизация",
        description = "Авторизация пользователя, можно войти как по почте так и просто по юзернейму"
    )
    @PostMapping("/authenticate")
    fun authenticateUser(
        @RequestBody @Valid authRequest: AuthRequestDTO,
        response: HttpServletResponse
    ): ResponseEntity<AuthResponseDTO> {
        val authResponse = authenticationService.authenticate(authRequest, response)
        return ResponseEntity.ok(authResponse)
    }
}
