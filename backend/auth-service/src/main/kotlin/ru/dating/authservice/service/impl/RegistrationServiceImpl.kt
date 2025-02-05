package ru.dating.authservice.service.impl

import org.springframework.stereotype.Service
import ru.dating.authservice.dto.UserRequestDTO
import ru.dating.authservice.dto.UserResponseDTO
import ru.dating.authservice.entity.User
import ru.dating.authservice.enums.Provider
import ru.dating.authservice.service.interfaces.ActivationService
import ru.dating.authservice.service.interfaces.RegistrationService
import ru.dating.authservice.service.interfaces.UserService
import java.time.LocalDateTime

@Service
class RegistrationServiceImpl(
    private val userService: UserService,
    private val activationService: ActivationService
) : RegistrationService {

    override fun register(request: UserRequestDTO): UserResponseDTO {

        val rawPassword = request.password ?: throw IllegalArgumentException("Password is required")

        val user = userService.registerUser(
            email = request.email,
            username = request.username,
            rawPassword = rawPassword
        )

        val activationToken = activationService.generateAndSaveActivationToken(user)

        activationService.sendActivationEmail(user, activationToken)

        return UserResponseDTO(
            role = user.roles,
            provider = user.provider,
            enabled = user.enabled,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
    }

}
