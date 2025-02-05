package ru.dating.authservice.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import org.springframework.validation.annotation.Validated

@Validated
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class UserRequestDTO(

    @field:NotEmpty(message = "username cannot be empty")
    @field:NotBlank(message = "username cannot has blank")
    var username: String,

    @field:Email(message = "email is not formatted")
    @field:NotEmpty(message = "email cannot be empty")
    @field:NotBlank(message = "email cannot has blank")
    var email: String,

    var password: String?
)