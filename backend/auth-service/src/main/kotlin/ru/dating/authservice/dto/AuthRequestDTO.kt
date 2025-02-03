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
data class AuthRequestDTO(

    //var username: String? = null,

    //@field:Email(message = "email is not formatted")
    //var email: String? = null,

    @field:NotBlank
    @field:NotEmpty
    var indentifier: String,

    @field:NotEmpty(message = "password cannot be empty")
    @field:NotBlank(message = "password cannot has blank")
    @field:Size(min = 4, message = "password should be 4 chars long minimum")
    var password: String
)