package ru.dating.authservice.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import ru.dating.authservice.config.JwtConfig
import java.time.Instant
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

    fun generateToken(userDetails: UserDetails): String {
        val claims = mutableMapOf<String, Any>()
        return generateToken(claims, userDetails)
    }

    fun generateToken(
        claims: Map<String, Any>,
        userDetails: UserDetails
    ): String = buildToken(claims, userDetails, jwtConfig.expiration)

    private fun buildToken(
        extraClaims: Map<String, Any>,
        userDetails: UserDetails,
        jwtExpiration: Long
    ): String {
        val authorities = userDetails.authorities.toList()
        val now = Instant.now()

        return Jwts.builder()
            .claims(extraClaims)
            .subject(userDetails.username)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(jwtExpiration)))
            .claim("authorities", authorities)
            .signWith(getSignInKey())
            .compact()
    }

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return username != null &&
                username == userDetails.username &&
                !isTokenExpired(token)
    }

    fun generateRefreshToken(userDetails: UserDetails): String {
        return buildToken(emptyMap(), userDetails, jwtConfig.refreshExpiration)
    }

    private fun isTokenExpired(token: String): Boolean = extractExpiration(token).before(Date.from(Instant.now()))

    private fun extractExpiration(token: String): Date = extractClaim(token, Claims::getExpiration)


}
