package ru.dating.authservice.service.interfaces

import ru.dating.authservice.entity.Role
import ru.dating.authservice.entity.User
import ru.dating.authservice.exception.GlobalExceptionHandler

interface UserService {

    fun saveUser(user: User): User
    fun saveRole(role: Role): Role
    fun getUsers(): List<User>

    @Throws(GlobalExceptionHandler.UserAlreadyExistsException::class, IllegalStateException::class)
    fun registerUser(email: String, username: String, rawPassword: String): User

    fun findByEmail(email: String): User?
    fun findByUsername(username: String): User?
    fun findById(id: Long): User?

    fun enableUser(user: User)
    fun updatePassword(user: User, newPassword: String)
}
