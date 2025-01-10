package ru.dating.authservice.controller

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

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val registrationService: RegistrationService,
    private val authenticationService: AuthenticationService,
    private val logoutService: LogoutService,
    private val passwordRecoveryService: PasswordRecoveryService
) {

    @PostMapping("/register")
    fun registerUser(@RequestBody @Valid registrationRequest: UserRequestDTO): ResponseEntity<UserResponseDTO> {
        val response = registrationService.register(registrationRequest)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/authenticate")
    fun authenticateUser(
        @RequestBody @Valid authRequest: AuthRequestDTO,
        response: HttpServletResponse
    ): ResponseEntity<AuthResponseDTO> {
        val authResponse = authenticationService.authenticate(authRequest, response)
        return ResponseEntity.ok(authResponse)
    }

    @PostMapping("/refresh-token")
    fun refreshToken(
        @CookieValue("refreshToken") refreshToken: String,
        response: HttpServletResponse
    ): ResponseEntity<AuthResponseDTO> {
        val newTokens = authenticationService.refreshToken(refreshToken, response)
        return ResponseEntity.ok(newTokens)
    }

    @GetMapping("/activate-account")
    fun confirm(
        @RequestParam token: String,
    ): ResponseEntity<String> {
        registrationService.activateAccount(token)
        return ResponseEntity.ok("Account activated successfully")
    }

    @PostMapping("/password-recovery")
    fun sendPasswordRecoveryEmail(
        @RequestBody request: PasswordRecoveryRequestDTO
    ): ResponseEntity<String> {
        passwordRecoveryService.initiatePasswordRecovery(request.identifier)
        return ResponseEntity.ok("Password recovery email sent")
    }

    @PostMapping("/reset-password")
    fun resetPassword(
        @RequestBody @Valid request: PasswordResetRequestDTO
    ): ResponseEntity<String> {
        passwordRecoveryService.resetPassword(request.token, request.newPassword)
        return ResponseEntity.ok("Password has been reset successfully")
    }

    @PostMapping("/logout")
    fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication?
    ): ResponseEntity<String> {
        logoutService.logout(request, response, authentication)
        return ResponseEntity.ok("Logged out successfully")
    }
}
