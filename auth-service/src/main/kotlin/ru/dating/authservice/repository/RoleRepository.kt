package ru.dating.authservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.dating.authservice.entity.Role


interface RoleRepository: JpaRepository<Role, Long> {
    fun findByName(name: String): Role?
}