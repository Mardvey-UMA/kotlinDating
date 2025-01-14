package ru.dating.authservice.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import ru.dating.authservice.entity.Role
import ru.dating.authservice.enums.Provider
import ru.dating.authservice.enums.UserRole
import java.time.LocalDateTime

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class UserResponseDTO(
    var role: MutableSet<Role>, // Тут поменять
    var provider: Provider,
    var enabled: Boolean,
    var vkId: Long? = null,
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime? = null,
    )
