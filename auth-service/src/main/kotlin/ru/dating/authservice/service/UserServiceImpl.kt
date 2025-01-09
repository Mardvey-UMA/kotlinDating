package ru.dating.authservice.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import ru.dating.authservice.entity.Role
import ru.dating.authservice.entity.User
import ru.dating.authservice.repository.RoleRepository
import ru.dating.authservice.repository.UserRepository

@Service
@Transactional
class UserServiceImpl(
        private val userRepository: UserRepository,
        private val roleRepository: RoleRepository
    ): UserService {

    override fun saveUser(user: User): User = userRepository.save(user)

    override fun saveRole(role: Role): Role = roleRepository.save(role)

    override fun getUsers(): List<User> = userRepository.findAll()
}