package ru.dating.authservice.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.dating.authservice.service.interfaces.ActivationService

@Tag(
    name = "Активация аккаунта при регистрации",
    description = "Эндпоинт для отправки кода подтверждения для активации аккаунта")
@RequestMapping("/api/auth/activate-account")
@RestController
class ActivationController(
    private val activationService: ActivationService
) {
    @Operation(
        summary = "Активация аккаунта",
        description = "Запрос на активацию аккаунта (прикладывание кода из почты)")
    @GetMapping
    fun confirm(
        @RequestParam token: String,
    ): ResponseEntity<String> {
        activationService.activateAccount(token)
        return ResponseEntity.ok("Account activated successfully")
    }

}