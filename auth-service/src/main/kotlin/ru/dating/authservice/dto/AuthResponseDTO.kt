package ru.dating.authservice.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.time.Instant
import java.time.LocalDateTime

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class AuthResponseDTO (
    var accessToken: String,
    var accessExpiresAt: LocalDateTime? = null,
    var issuedAt: LocalDateTime? = null,
    var refreshToken: String? = null,
    var refreshExpiresAt: LocalDateTime? = null,
)
