package ru.dating.authservice.service

import jakarta.mail.MessagingException
import org.springframework.beans.factory.annotation.Qualifier
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
import ru.dating.authservice.enums.UserRole
import ru.dating.authservice.entity.User
import ru.dating.authservice.enums.EmailTemplateName
import ru.dating.authservice.enums.Provider
import ru.dating.authservice.repository.RoleRepository
import ru.dating.authservice.repository.MailTokenRepository
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
    @Qualifier("application.security.jwt-ru.dating.authservice.config.JwtConfig") private val jwtConfig: JwtConfig,
) {
    fun register(request: UserRequestDTO) : UserResponseDTO {
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

    fun authenticate(request: AuthRequestDTO): AuthResponseDTO {
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
        return AuthResponseDTO(
            accessToken = jwtToken,
            issuedAt = LocalDateTime.now(),
            accessExpiresAt = LocalDateTime.now().plusSeconds(jwtConfig.expiration),
            refreshToken = "MOKE",
            refreshExpiresAt = LocalDateTime.now().plusSeconds(jwtConfig.expiration)
        )
    }
    private fun sendValidationEmail(user: User) {
        val newToken = generateAndSaveActivationToken(user)
        emailService.sendEmail(
            to = user.name,
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
}