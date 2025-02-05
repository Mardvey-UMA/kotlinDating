package ru.dating.authservice.service.impl

import jakarta.transaction.Transactional
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import ru.dating.authservice.dto.UserRequestDTO
import ru.dating.authservice.dto.UserResponseDTO
import ru.dating.authservice.entity.Role
import ru.dating.authservice.entity.User
import ru.dating.authservice.enums.Provider
import ru.dating.authservice.enums.UserRole
import ru.dating.authservice.exception.GlobalExceptionHandler
import ru.dating.authservice.repository.RoleRepository
import ru.dating.authservice.repository.UserRepository
import ru.dating.authservice.service.interfaces.UserService
import java.time.LocalDateTime

@Service
@Transactional
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder
): UserService {

    override fun saveUser(user: User): User = userRepository.save(user)

    override fun saveRole(role: Role): Role = roleRepository.save(role)

    override fun getUsers(): List<User> = userRepository.findAll()

    override fun registerUser(email: String, username: String, rawPassword: String): User {
        if (userRepository.findByEmail(email) != null) {
            throw GlobalExceptionHandler.UserAlreadyExistsException("Email already in use")
        }
        if (userRepository.findByUsername(username) != null) {
            throw GlobalExceptionHandler.UserAlreadyExistsException("Username already in use")
        }

        val userRole = roleRepository.findByName(UserRole.USER.toString())
            ?: throw IllegalStateException("Role USER not found")

        val user = User(
            email = email,
            username = username,
            password = passwordEncoder.encode(rawPassword),
            accountLocked = false,
            enabled = false,
            roles = mutableSetOf(userRole),
            vkId = null,
            provider = Provider.PASSWORD,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )
        return userRepository.save(user)
    }

    override fun registerVkUser(userRequest: UserRequestDTO, vkId: Long): User {
        if (userRepository.findByVkId(vkId) != null) {
            throw GlobalExceptionHandler.UserAlreadyExistsException("VK ID already exists INVALID_VK_ID")
        }

        val email = userRequest.email.ifEmpty { "${vkId}@vk.com" }

        val userRole = roleRepository.findByName(UserRole.USER.toString())
            ?: throw GlobalExceptionHandler.UserAlreadyExistsException("Role USER not found")

        val user = User(
            email = email,
            username = userRequest.username,
            provider = Provider.VK,
            enabled = true, // VK по умолчанию активированы
            accountLocked = false,
            roles = mutableSetOf(userRole),
            vkId = vkId,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            password = null.toString()
        )
        userRepository.save(user)

        return user
    }

    override fun findByEmail(email: String): User? = userRepository.findByEmail(email)

    override fun findByUsername(username: String): User? = userRepository.findByUsername(username)

    override fun findById(id: Long): User? = userRepository.findById(id).orElse(null)

    override fun findByVkId(vkId: Long): User? = userRepository.findByVkId(vkId)

    override fun enableUser(user: User) {
        user.enabled = true
        userRepository.save(user)
    }

    override fun updatePassword(user: User, newPassword: String) {
        if (user.provider != Provider.PASSWORD) {
            throw IllegalStateException("VK User does not have a password")
        }
        user.password = passwordEncoder.encode(newPassword)
        userRepository.save(user)
    }
}