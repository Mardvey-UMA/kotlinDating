package ru.dating.authservice.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.dating.authservice.service.interfaces.ActivationService

@Tag(name = "User")
@RequestMapping("/api/activate-account")
@RestController
class ActivationController(
    private val activationService: ActivationService
) {
    @GetMapping
    fun confirm(
        @RequestParam token: String,
    ): ResponseEntity<String> {
        activationService.activateAccount(token)
        return ResponseEntity.ok("Account activated successfully")
    }

}