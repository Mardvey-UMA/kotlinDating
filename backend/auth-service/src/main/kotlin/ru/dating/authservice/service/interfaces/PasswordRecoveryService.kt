package ru.dating.authservice.service.interfaces

interface PasswordRecoveryService {
    fun initiatePasswordRecovery(identifier: String)
    fun resetPassword(token: String, newPassword: String)
}
