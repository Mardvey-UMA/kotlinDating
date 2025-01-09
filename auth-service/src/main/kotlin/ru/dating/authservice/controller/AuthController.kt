package ru.dating.authservice.controller

import jakarta.mail.MessagingException
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.dating.authservice.dto.AuthRequestDTO
import ru.dating.authservice.dto.AuthResponseDTO
import ru.dating.authservice.dto.UserRequestDTO
import ru.dating.authservice.dto.UserResponseDTO
import ru.dating.authservice.service.AuthenticationService
import kotlin.jvm.Throws

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthenticationService
) {
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun registerUser(@RequestBody @Valid registrationRequest: UserRequestDTO) : ResponseEntity<UserResponseDTO> {
        val response = authService.register(registrationRequest)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
    @PostMapping("/authenticate")
    fun authenticateUser(@RequestBody @Valid authRequest: AuthRequestDTO) : ResponseEntity<AuthResponseDTO> {
        return ResponseEntity.ok(authService.authenticate(authRequest))
    }

    @Throws(MessagingException::class)
    @GetMapping("/activate-account")
    fun confirm(
        @RequestParam token: String,
    ){
        authService.activateAccount(token)
    }

}
