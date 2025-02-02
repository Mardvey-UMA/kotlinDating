package ru.dating.authservice.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.dating.authservice.dto.PasswordRecoveryRequestDTO
import ru.dating.authservice.dto.PasswordResetRequestDTO
import ru.dating.authservice.service.impl.PasswordRecoveryServiceImpl

@Tag(
    name = "Восстановление пароля",
    description = "Все что касается восстановления аккаунта")
@RequestMapping("/api/auth/password")
@RestController
class PasswordController(
    private val passwordRecoveryService: PasswordRecoveryServiceImpl
) {
    @Operation(
        summary = "Запрос на восстановление пароля по почте",
        description = "После отправки запроса на почту отправляется код подтверждения"
    )
    @PostMapping("/recovery")
    fun sendPasswordRecoveryEmail(
        @RequestBody request: PasswordRecoveryRequestDTO
    ): ResponseEntity<String> {
        passwordRecoveryService.initiatePasswordRecovery(request.identifier)
        return ResponseEntity.ok("Password recovery email sent")
    }

    @Operation(
        summary = "Сброс пароля",
        description = "Сброс пароля по ранее полученному на почту коду сброса"
    )
    @PostMapping("/reset")
    fun resetPassword(
        @RequestBody @Valid request: PasswordResetRequestDTO
    ): ResponseEntity<String> {
        passwordRecoveryService.resetPassword(request.token, request.newPassword)
        return ResponseEntity.ok("Password has been reset successfully")
    }

}