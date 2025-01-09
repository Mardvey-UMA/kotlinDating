package ru.dating.authservice.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class UserRequestDTO(

    @NotEmpty(message = "username cannot be empty")
    @NotBlank(message = "username cannot has blank")
    var username: String,

    @Email(message = "email is not formatted")
    @NotEmpty(message = "email cannot be empty")
    @NotBlank(message = "email cannot has blank")
    var email: String,

    @NotEmpty(message = "password cannot be empty")
    @NotBlank(message = "password cannot has blank")
    @Size(min = 4, message = "password should be 4 chars long minimum")
    var password: String
)
/*
    Убрал Имя/Фамилию так как при регистрации они ни к чему (имхо)
    TODO(Добавить подтверждение аккаунта через почту)
*/