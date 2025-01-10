package ru.dating.authservice.repository

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import ru.dating.authservice.entity.User

interface UserRepository: JpaRepository<User, Long> {
    @EntityGraph(attributePaths = ["roles"])
    fun findByEmail(email: String): User?

    @EntityGraph(attributePaths = ["roles"])
    fun findByUsername(username: String): User?

    @EntityGraph(attributePaths = ["roles"])
    fun findByVkId(vkId: Long): User?
}
