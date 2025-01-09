package ru.dating.authservice.service

import ru.dating.authservice.dto.UserRequestDTO
import ru.dating.authservice.dto.UserResponseDTO
import ru.dating.authservice.entity.Role
import ru.dating.authservice.entity.User

interface UserService {

    fun saveUser(user: User): User
    fun saveRole(role: Role): Role
    fun getUsers(): List<User>

}
