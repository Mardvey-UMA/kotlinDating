package ru.dating.authservice.service.impl

import org.springframework.stereotype.Service
import ru.dating.authservice.entity.MailToken
import ru.dating.authservice.entity.User
import ru.dating.authservice.enums.MailTokenType
import ru.dating.authservice.repository.MailTokenRepository
import ru.dating.authservice.config.EmailConfig
import ru.dating.authservice.enums.EmailTemplateName
import ru.dating.authservice.exception.GlobalExceptionHandler
import ru.dating.authservice.service.interfaces.ActivationService
import ru.dating.authservice.service.EmailService
import ru.dating.authservice.service.interfaces.UserService
import java.security.SecureRandom
import java.time.LocalDateTime

@Service
class ActivationServiceImpl(
    private val mailTokenRepository: MailTokenRepository,
    private val userService: UserService,
    private val emailService: EmailService,
    private val emailConfig: EmailConfig
) : ActivationService {

    override fun generateAndSaveActivationToken(user: User): String {
        deactivateExistingTokens(user, MailTokenType.CONFIRM)

        val generatedToken = generateActivationCode()
        val mailToken = MailToken(
            token = generatedToken,
            createdAt = LocalDateTime.now(),
            expiresAt = LocalDateTime.now().plusSeconds(emailConfig.activationTokenExpiration),
            user = user,
            tokenType = MailTokenType.CONFIRM
        )
        mailTokenRepository.save(mailToken)
        return generatedToken
    }

    override fun activateAccount(token: String) {
        val savedMailToken: MailToken = mailTokenRepository.findByToken(token)
            ?: throw GlobalExceptionHandler.InvalidTokenException("Invalid activation token")

        if (LocalDateTime.now().isAfter(savedMailToken.expiresAt) || !savedMailToken.enabled) {
            throw GlobalExceptionHandler.InvalidTokenException("Activation token expired or already used")
        }

        val user = savedMailToken.user
        userService.enableUser(user)
        savedMailToken.validatedAt = LocalDateTime.now()
        savedMailToken.enabled = false
        mailTokenRepository.save(savedMailToken)
    }

    override fun sendActivationEmail(user: User, token: String) {
        emailService.sendEmail(
            to = user.email,
            username = user.username,
            emailTemplate = EmailTemplateName.ACTIVATE_ACCOUNT,
            confirmationUrl = emailConfig.activationUrl,
            activationCode = token,
            subject = "Account Activation"
        )
    }

    private fun deactivateExistingTokens(user: User, tokenType: MailTokenType) {
        val activeTokens = mailTokenRepository.findByUserIdAndEnabledAndTokenType(
            userId = user.id!!,
            enabled = true,
            tokenType = tokenType
        )
        activeTokens.forEach { it.enabled = false }
        mailTokenRepository.saveAll(activeTokens)
    }

    private fun generateActivationCode(length: Int = 6): String {
        val secureRandom = SecureRandom()
        return (1..length)
            .map { secureRandom.nextInt(10) }
            .joinToString("")
    }
}
