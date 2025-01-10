package ru.dating.authservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ru.dating.authservice.entity.Token
import ru.dating.authservice.enums.TokenType

interface TokenRepository : JpaRepository<Token, Long> {
    @Query(
        value = """
        select t from Token t 
        where t.user.email = :email and t.expired = false and t.revoked = false
        """
    )
    fun findAllValidTokenByUserEmail (email: String):Set<Token>

    @Query(
        value = """
        select t from Token t inner join User u
        on t.user.id = u.id
        where u.id = :id and t.expired = false and t.revoked = false
        """
    )
    fun findAllValidTokenByUser (id: Long):Set<Token>

    @Query("SELECT t FROM Token t " +
            "WHERE t.user.id = :userId AND t.expired = false AND t.revoked = false AND t.tokenType = 'REFRESH'")
    fun findValidRefreshTokensByUser(userId: Long): Set<Token>

    fun findByToken (token: String): Token?
}