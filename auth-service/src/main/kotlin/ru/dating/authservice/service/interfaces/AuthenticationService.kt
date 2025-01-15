package ru.dating.authservice.service.interfaces

import jakarta.servlet.http.HttpServletResponse
import ru.dating.authservice.dto.AuthRequestDTO
import ru.dating.authservice.dto.AuthResponseDTO

interface AuthenticationService {
    fun authenticate(request: AuthRequestDTO, response: HttpServletResponse): AuthResponseDTO
}
