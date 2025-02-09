package ru.dating.authservice.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.Cookie
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import ru.dating.authservice.config.JwtConfig
import ru.dating.authservice.entity.User
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService(private val jwtConfig: JwtConfig) {

    fun getSignInKey(): SecretKey {
        val keyBytes = Decoders.BASE64.decode(jwtConfig.secretKey)
        return Keys.hmacShaKeyFor(keyBytes)
    }

    fun extractUsername(token: String): String? = extractClaim(token) { claims: Claims -> claims.subject }

    fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T {
        val claims = extractAllClaims(token)
        return claimsResolver.invoke(claims)
    }

    fun extractAllClaims(token: String): Claims =
        Jwts.parser()
            .verifyWith(getSignInKey())
            .build()
            .parseSignedClaims(token)
            .payload

    private fun buildToken(
        extraClaims: Map<String, Any>,
        userDetails: UserDetails,
        jwtExpiration: Long
    ): String {
        val now = Instant.now()
        return Jwts.builder()
            .claims(extraClaims)
            .subject(userDetails.username)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(jwtExpiration)))
            .signWith(getSignInKey())
            .compact()
    }

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return username != null &&
                username == userDetails.username &&
                !isTokenExpired(token)
    }

    fun createHttpOnlyCookie(name: String, value: String): Cookie {
        val cookie = Cookie(name, value)
        cookie.isHttpOnly = true
        cookie.maxAge = jwtConfig.expiration.toInt()
        cookie.path = "/"
        return cookie
    }

    private fun isTokenExpired(token: String): Boolean = extractExpiration(token).before(Date.from(Instant.now()))

    fun extractExpiration(token: String): Date = extractClaim(token, Claims::getExpiration)

    fun generateAccessToken(user: UserDetails): String {
        val userEntity = user as User
        val claims = mapOf<String, Any>(
            "roles" to user.authorities.map { it.authority },
            "token_type" to "ACCESS",
            "username" to userEntity.username,
            "user_id" to userEntity.id!!,
            "sub" to userEntity.username
        )
        return buildToken(claims, user, jwtConfig.expiration)
    }

    fun generateRefreshToken(user: UserDetails): String {
        val claims = mapOf("type" to "refresh")
        return buildToken(claims, user, jwtConfig.refreshExpiration)
    }
    fun isRefreshTokenValid(token: String): Boolean {
        val claims = extractAllClaims(token)
        return claims["type"] == "refresh" && !isTokenExpired(token)
    }

    /////////////////////////////////////////////
    fun extractExpirationLocalDateTime(token: String): LocalDateTime? {
        return try {
            val expirationDate = extractExpiration(token)
            expirationDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
        } catch (e: Exception) {
            null
        }
    }
}
