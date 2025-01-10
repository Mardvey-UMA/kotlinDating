package ru.dating.authservice.controller

import jakarta.mail.MessagingException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.dating.authservice.dto.*
import ru.dating.authservice.service.AuthenticationService
import ru.dating.authservice.service.LogoutService
import kotlin.jvm.Throws

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthenticationService,
    private val logoutService: LogoutService
) {
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun registerUser(@RequestBody @Valid registrationRequest: UserRequestDTO) : ResponseEntity<UserResponseDTO> {
        val response = authService.register(registrationRequest)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/authenticate")
    fun authenticateUser(
        @RequestBody @Valid authRequest: AuthRequestDTO,
        response: HttpServletResponse
    ): ResponseEntity<AuthResponseDTO> {
        return ResponseEntity.ok(authService.authenticate(authRequest, response))
    }

    @PostMapping("/refresh-token")
    fun refreshToken(
        @CookieValue("refreshToken") refreshToken: String,
        response: HttpServletResponse
    ): ResponseEntity<AuthResponseDTO> {
        val newTokens = authService.refreshToken(refreshToken, response)
        return ResponseEntity.ok(newTokens)
    }

    @Throws(MessagingException::class)
    @GetMapping("/activate-account")
    fun confirm(
        @RequestParam token: String,
    ){
        authService.activateAccount(token)
    }

    @Throws(MessagingException::class)
    @PostMapping("/password-recovery")
    fun sendPasswordRecoveryEmail(
        @RequestBody request: PasswordRecoveryRequestDTO
    ): ResponseEntity<String> {
        authService.sendPasswordRecoveryEmail(request.identifier)
        return ResponseEntity.ok("Password recovery email sent")
    }

    @PostMapping("/reset-password")
    fun resetPassword(
        @RequestBody @Valid request: PasswordResetRequestDTO
    ): ResponseEntity<String> {
        authService.resetPassword(request.token, request.newPassword)
        return ResponseEntity.ok("Password has been reset successfully")
    }

    @GetMapping("/logout")
    fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): HttpStatus {
        logoutService.logout(request, response, null)
        return HttpStatus.OK
    }

}
