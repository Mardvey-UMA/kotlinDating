package ru.dating.authservice.dto

import jakarta.validation.constraints.NotBlank
import org.springframework.validation.annotation.Validated


@Validated
data class PasswordRecoveryRequestDTO(
    @field:NotBlank(message = "Identifier must not be blank")
    val identifier: String
)
