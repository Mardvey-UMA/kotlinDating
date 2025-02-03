package ru.dating.authservice.service.impl

import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import ru.dating.authservice.dto.AuthRequestDTO
import ru.dating.authservice.dto.AuthResponseDTO
import ru.dating.authservice.entity.User
import ru.dating.authservice.config.JwtConfig
import ru.dating.authservice.enums.CookieName
import ru.dating.authservice.exception.GlobalExceptionHandler
import ru.dating.authservice.service.interfaces.AuthenticationService
import ru.dating.authservice.service.JwtService
import ru.dating.authservice.service.interfaces.TokenService
import ru.dating.authservice.service.interfaces.UserService
import java.time.LocalDateTime

@Service
class AuthenticationServiceImpl(
    private val authenticationManager: AuthenticationManager,
    private val userService: UserService,
    private val jwtService: JwtService,
    private val tokenService: TokenService,
    private val jwtConfig: JwtConfig
) : AuthenticationService {

    override fun authenticate(request: AuthRequestDTO, response: HttpServletResponse): AuthResponseDTO {

        // TODO Исправить на identifier чтобы проверял это email или username

        /*if ((request.username.isNullOrBlank() && request.email.isNullOrBlank()) ||
            (!request.username.isNullOrBlank() && !request.email.isNullOrBlank())
        ) {
            throw UsernameNotFoundException("Either username or email must be provided, but not both")
        }

        val userEntity: User = if (!request.username.isNullOrBlank()) {
            userService.findByUsername(request.username!!)
                ?: throw UsernameNotFoundException("User with username ${request.username} not found")
        } else {
            userService.findByEmail(request.email!!)
                ?: throw UsernameNotFoundException("User with email ${request.email} not found")
        }

        if (!userEntity.enabled) {
            throw UsernameNotFoundException("User account is not activated")
        }*/

        val auth = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                userEntity.username,
                request.password,
            )
        )
        val user = auth.principal as User

        val accessToken: String = jwtService.generateAccessToken(user)
        val refreshToken: String = jwtService.generateRefreshToken(user)

        response.addCookie(jwtService.createHttpOnlyCookie(CookieName.ACCESS_TOKEN.name, accessToken))
        response.addCookie(jwtService.createHttpOnlyCookie(CookieName.REFRESH_TOKEN.name, refreshToken))

        tokenService.saveRefreshToken(user, refreshToken)

        return AuthResponseDTO(
            accessToken = accessToken,
            issuedAt = LocalDateTime.now(),
            accessExpiresAt = LocalDateTime.now().plusSeconds(jwtConfig.expiration),
            refreshToken = refreshToken,
            refreshExpiresAt = LocalDateTime.now().plusSeconds(jwtConfig.refreshExpiration)
        )
    }
}
