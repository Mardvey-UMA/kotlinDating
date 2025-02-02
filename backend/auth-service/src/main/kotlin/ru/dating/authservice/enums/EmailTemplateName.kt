package ru.dating.authservice.enums

enum class EmailTemplateName(
    val templateName: String
) {
    ACTIVATE_ACCOUNT("activate_account"),
    RECOVERY_PASSWORD("recovery_password")
}
