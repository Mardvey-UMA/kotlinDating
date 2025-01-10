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
    var vkId: String? = null,
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime? = null,
    )
/*
    После регистрации, нам не нужен id, username, слишком небезопасно
    Думаю достаточно того, что я описал
    TODO(
     Скорее всего придется добавить поля в духе "Заблокирован"
     после реализации какой-то логики
     По блокировке пользователей
     )
*/