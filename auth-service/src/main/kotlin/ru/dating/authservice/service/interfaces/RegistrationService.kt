package ru.dating.authservice.service.interfaces

import ru.dating.authservice.dto.UserRequestDTO
import ru.dating.authservice.dto.UserResponseDTO

interface RegistrationService {
    fun register(request: UserRequestDTO): UserResponseDTO
}
