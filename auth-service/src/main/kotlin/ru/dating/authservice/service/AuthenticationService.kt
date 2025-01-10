package ru.dating.authservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.mail.MessagingException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import ru.dating.authservice.config.EmailConfig
import ru.dating.authservice.config.JwtConfig
import ru.dating.authservice.dto.AuthRequestDTO
import ru.dating.authservice.dto.AuthResponseDTO
import ru.dating.authservice.dto.UserRequestDTO
import ru.dating.authservice.dto.UserResponseDTO
import ru.dating.authservice.entity.MailToken
import ru.dating.authservice.entity.Token
import ru.dating.authservice.enums.UserRole
import ru.dating.authservice.entity.User
import ru.dating.authservice.enums.EmailTemplateName
import ru.dating.authservice.enums.Provider
import ru.dating.authservice.enums.TokenType
import ru.dating.authservice.exception.GlobalExceptionHandler
import ru.dating.authservice.repository.RoleRepository
import ru.dating.authservice.repository.MailTokenRepository
import ru.dating.authservice.repository.TokenRepository
import ru.dating.authservice.repository.UserRepository
import java.security.SecureRandom
import java.time.LocalDateTime
import kotlin.jvm.Throws

@Service
class AuthenticationService(
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder,
    private val userRepository: UserRepository,
    private val mailTokenRepository: MailTokenRepository,
    private val emailService: EmailService,
    private val emailConfig: EmailConfig,
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
    private val tokenRepository: TokenRepository,
    @Qualifier("application.security.jwt-ru.dating.authservice.config.JwtConfig") private val jwtConfig: JwtConfig,
) {
    fun register(request: UserRequestDTO) : UserResponseDTO {
        if (userRepository.findByEmail(request.email) != null) {
            throw GlobalExceptionHandler.UserAlreadyExistsException("Email already in use")
        }
        if (userRepository.findByUsername(request.username) != null) {
            throw GlobalExceptionHandler.UserAlreadyExistsException("Username already in use")
        }
        
        val userRole = roleRepository.findByName(UserRole.USER.toString())
            ?: throw IllegalStateException("Role USER not found")
        val user = User(
            email = request.email,
            username = request.username,
            password = passwordEncoder.encode(request.password),
            accountLocked = false,
            enabled = false,
            roles = mutableSetOf(userRole),
            vkId = null,
            provider = Provider.PASSWORD
        )
        userRepository.save(user)
        sendValidationEmail(user);
        return UserResponseDTO(
            role = mutableSetOf(userRole),
            provider = Provider.PASSWORD,
            enabled = false,
            createdAt = LocalDateTime.now()
        )
    }

    fun authenticate(request: AuthRequestDTO, response: HttpServletResponse): AuthResponseDTO {
        /*
        TODO(Здесь нужно нормальные исключения раскидать)
         */
        if ((request.username.isNullOrBlank() && request.email.isNullOrBlank()) ||
            (!request.username.isNullOrBlank() && !request.email.isNullOrBlank())
        ) {
            throw UsernameNotFoundException("Either username or email must be provided, but not both")
        }

        val userEntity = if (!request.username.isNullOrBlank()) {
            userRepository.findByUsername(request.username!!)
                ?: throw UsernameNotFoundException("User with username ${request.username} not found")
        } else {
            userRepository.findByEmail(request.email!!)
                ?: throw UsernameNotFoundException("User with email ${request.email} not found")
        }
        if (!userEntity.enabled) {
            throw UsernameNotFoundException("User account is not activated")
        }
        val auth = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                userEntity.username,
                request.password,
            )
        )
        val claims : MutableMap<String, Any> = HashMap()
        val user = auth.principal as User
        claims["email"] = user.username
        val jwtToken = jwtService.generateToken(claims, user)
        val refreshToken = jwtService.generateRefreshToken(user)

        response.addCookie(jwtService.createHttpOnlyCookie("accessToken", jwtToken))
        response.addCookie(jwtService.createHttpOnlyCookie("refreshToken", refreshToken))

        saveUserToken(user, jwtToken)
        return AuthResponseDTO(
            accessToken = jwtToken,
            issuedAt = LocalDateTime.now(),
            accessExpiresAt = LocalDateTime.now().plusSeconds(jwtConfig.expiration),
            refreshToken = refreshToken,
            refreshExpiresAt = LocalDateTime.now().plusSeconds(jwtConfig.refreshExpiration)
        )
    }
    private fun sendValidationEmail(user: User) {
        val newToken = generateAndSaveActivationToken(user)
        emailService.sendEmail(
            to = user.username,
            username = user.name,
            emailTemplate = EmailTemplateName.ACTIVATE_ACCOUNT,
            confirmationUrl = emailConfig.activationUrl,
            activationCode = newToken,
            subject = "Account activation"
        )
    }
    private fun generateAndSaveActivationToken(user: User): String {
        //generate token
        val generatedToken = generateActivationCode()
        val mailToken: MailToken = MailToken(
            token = generatedToken,
            createdAt = LocalDateTime.now(),
            expiresAt = LocalDateTime.now().plusSeconds(emailConfig.activationTokenExpiration),
            user = user
        )
        mailTokenRepository.save(mailToken)
        return generatedToken
    }

    fun sendPasswordRecoveryEmail(identifier: String) {
        val user = if (identifier.contains("@")) {
            userRepository.findByEmail(identifier)
                ?: throw UsernameNotFoundException("User with email $identifier not found")
        } else {
            userRepository.findByUsername(identifier)
                ?: throw UsernameNotFoundException("User with username $identifier not found")
        }

        val recoveryToken = generateAndSaveRecoveryToken(user)
        emailService.sendEmail(
            to = user.username,
            username = user.name,
            emailTemplate = EmailTemplateName.RECOVERY_PASSWORD,
            confirmationUrl = emailConfig.activationUrl,
            activationCode = recoveryToken,
            subject = "Password Recovery"
        )
    }

    private fun generateAndSaveRecoveryToken(user: User): String {
        val token = generateActivationCode()
        val mailToken = MailToken(
            token = token,
            createdAt = LocalDateTime.now(),
            expiresAt = LocalDateTime.now().plusSeconds(emailConfig.activationTokenExpiration),
            user = user
        )
        mailTokenRepository.save(mailToken)
        return token
    }


    private fun generateActivationCode(length: Int = 6): String {
        val secureRandom = SecureRandom()
        val codeBuilder = StringBuilder(length)
        repeat(length) {
            codeBuilder.append(secureRandom.nextInt(10))
        }
        return codeBuilder.toString()
    }

    @Throws(MessagingException::class)
    fun activateAccount(token: String) {
        val savedMailToken: MailToken = mailTokenRepository.findByToken(token)
            ?: throw UsernameNotFoundException("Invalid token")
        if (LocalDateTime.now().isAfter(savedMailToken.expiresAt)){
            sendValidationEmail(savedMailToken.user)
            throw UsernameNotFoundException("Activation token expired, new token send!!")
        }
        val user = userRepository.findByEmail(savedMailToken.user.name)
            ?: throw UsernameNotFoundException("User ${savedMailToken.user.name} not found")
        user.enabled = true
        userRepository.save(user)
        savedMailToken.validatedAt = LocalDateTime.now()
        mailTokenRepository.save(savedMailToken)
    }
    @Throws(MessagingException::class)
    fun resetPassword(token: String, newPassword: String) {
        val mailToken = mailTokenRepository.findByToken(token)
            ?: throw UsernameNotFoundException("Invalid token")

        if (LocalDateTime.now().isAfter(mailToken.expiresAt)) {
            throw IllegalStateException("Token has expired")
        }

        val user = mailToken.user
        user.password = passwordEncoder.encode(newPassword)
        userRepository.save(user)

        mailToken.validatedAt = LocalDateTime.now()
        mailTokenRepository.save(mailToken)
    }

    private fun revokeAllUserTokens(user: User) {
        val validUserTokens = tokenRepository.findAllValidTokenByUserEmail(user.username)
        if (validUserTokens.isEmpty()) return

        validUserTokens.forEach { token ->
            token.expired = true
            token.revoked = true
        }
        tokenRepository.saveAll(validUserTokens)
    }

    private fun saveUserToken(user: User, jwtToken: String) {
        val token = Token(
            user = user,
            token = jwtToken,
            tokenType = TokenType.BEARER,
            expired = false,
            revoked = false
        )
        tokenRepository.save(token)
    }

    fun refreshToken(refreshToken: String, response: HttpServletResponse): AuthResponseDTO {
        val userEmail = jwtService.extractUsername(refreshToken)
            ?: throw UsernameNotFoundException("Invalid refresh token")

        val user = userRepository.findByEmail(userEmail)
            ?: throw UsernameNotFoundException("User not found")

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw UsernameNotFoundException("Refresh token is invalid or expired")
        }

        val newAccessToken = jwtService.generateToken(user)
        response.addCookie(jwtService.createHttpOnlyCookie("accessToken", newAccessToken))
        revokeAllUserTokens(user)
        saveUserToken(user, newAccessToken)
        return AuthResponseDTO(
            accessToken = newAccessToken,
            issuedAt = LocalDateTime.now(),
            accessExpiresAt = LocalDateTime.now().plusSeconds(jwtConfig.expiration),
            refreshToken = refreshToken,
            refreshExpiresAt = LocalDateTime.now().plusSeconds(jwtConfig.refreshExpiration)
        )
    }
}