package ru.dating.authservice.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import jakarta.validation.constraints.NotBlank
import org.springframework.validation.annotation.Validated

@Validated
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class PasswordResetRequestDTO(
    @field:NotBlank(message = "Token must not be blank")
    val token: String,

    @field:NotBlank(message = "New password must not be blank")
    val newPassword: String
)
