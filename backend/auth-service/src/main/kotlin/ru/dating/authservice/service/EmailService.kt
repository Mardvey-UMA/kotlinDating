package ru.dating.authservice.service

import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine
import ru.dating.authservice.config.EmailConfig
import ru.dating.authservice.enums.EmailTemplateName

@Service
class EmailService(
    private val mailSender: JavaMailSender,
    private val templateEngine: SpringTemplateEngine,
    private val emailConfig: EmailConfig
) {
    @Async
    fun sendEmail(to: String,
                  username: String,
                  emailTemplate: EmailTemplateName,
                  confirmationUrl: String,
                  activationCode: String,
                  subject: String)
    {
        val mimeMessage: MimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(mimeMessage,
            MimeMessageHelper.MULTIPART_MODE_MIXED
            )
        val properties = mutableMapOf<String, Any>()
        properties["username"] = username
        properties["confirmationUrl"] = confirmationUrl
        properties["activation_code"] = activationCode

        val context = Context() // ThymeLeaf Context
        context.setVariables(properties)

        helper.setFrom(emailConfig.emailAddressSender)
        helper.setTo(to)
        helper.setSubject(subject)

        val template = templateEngine.process(emailTemplate.templateName, context)

        helper.setText(template, true)

        mailSender.send(mimeMessage)
    }
}
