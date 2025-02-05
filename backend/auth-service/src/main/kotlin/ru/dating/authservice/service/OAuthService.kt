package ru.dating.authservice.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpMethod
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus

import ru.dating.authservice.config.VkApiConfig
import ru.dating.authservice.dto.AuthResponseDTO
import ru.dating.authservice.dto.UserRequestDTO
import ru.dating.authservice.entity.User
import ru.dating.authservice.enums.CookieName
import ru.dating.authservice.enums.Provider

import ru.dating.authservice.repository.UserRepository
import ru.dating.authservice.service.interfaces.TokenService
import ru.dating.authservice.service.interfaces.UserService
import java.net.URI
import java.time.LocalDateTime

@Service
class OAuthService(
    private val restTemplate: RestTemplate,
    private val userRepository: UserRepository,
    private val jwtService: JwtService,
    private val userService: UserService,
    private val objectMapper: ObjectMapper,
    private val vkApiConfig: VkApiConfig,
    private val tokenService: TokenService
) {

    fun authenticate(code: String, response: HttpServletResponse): AuthResponseDTO {
        // Поменять код на токен
        val tokenResponse = exchangeCodeForAccessToken(code)
        val accessTokenVk = tokenResponse["access_token"].asText() // access вкшный

        val userId = tokenResponse["user_id"].asText()
        val email = tokenResponse["email"]?.asText()

        // размениваем на id
        val userInfoResponse = getUserInfoVk(accessTokenVk, userId)
        val vkId = userInfoResponse["response"][0]["id"].asLong()

        // Проверяем есть ли такой
        val existingUser = userRepository.findByVkId(vkId)
        val user = existingUser ?: createNewUser(vkId, email)

        val refreshToken: String = jwtService.generateRefreshToken(user)
        val accessToken: String = jwtService.generateAccessToken(user)

        // Кидаем в кукисы
        response.addCookie(jwtService.createHttpOnlyCookie(CookieName.ACCESS_TOKEN.name, accessToken))
        response.addCookie(jwtService.createHttpOnlyCookie(CookieName.REFRESH_TOKEN.name, refreshToken))

        // Пишем рефрешку
        tokenService.saveRefreshToken(user, refreshToken)

        return AuthResponseDTO(
            accessToken = accessToken,
            refreshToken = refreshToken,
            issuedAt = LocalDateTime.now(),
            accessExpiresAt = jwtService.extractExpirationLocalDateTime(accessToken),
            refreshExpiresAt = jwtService.extractExpirationLocalDateTime(refreshToken)
        )
    }

    private fun exchangeCodeForAccessToken(code: String): JsonNode {
        val tokenUri = vkApiConfig.provider.vk.tokenUri
        // Формировка ссылки
        val uri = URI.create(
            "$tokenUri?client_id=${vkApiConfig.registration.vk.clientId}" +
                    "&client_secret=${vkApiConfig.registration.vk.clientSecret}" +
                    "&redirect_uri=${vkApiConfig.registration.vk.redirectUri}" +
                    "&code=$code"
        )
        // Прокидываем GET
        val response: ResponseEntity<String> = restTemplate.exchange(
            uri,
            HttpMethod.GET,
            null,
            String::class.java
        )
        // Тело ответа
        return parseJson(response.body!!)
    }

    private fun getUserInfoVk(accessToken: String, userId: String): JsonNode {
        val userInfoUri = vkApiConfig.provider.vk.userInfoUri

        // Запрос на вытяжку инфы
        val uri = URI.create(
            "$userInfoUri?user_ids=$userId&fields=email&access_token=$accessToken&v=5.131"
        )

        val response: ResponseEntity<String> = restTemplate.exchange(
            uri,
            HttpMethod.GET,
            null,
            String::class.java
        )

        return parseJson(response.body!!)
    }

    private fun parseJson(json: String): JsonNode {
        return objectMapper.readTree(json)
    }

    private fun createNewUser(vkId: Long, email: String?): User {
        val userEmail = email ?: "${vkId}@vk.com"

        return userService.registerVkUser(
            UserRequestDTO(
                username = vkId.toString(),
                email = userEmail,
                password = null
            ),
            vkId
        )
    }
    fun vkLoginPageOpen(): ResponseEntity<String> {
        val uri = URI.create(
            "${vkApiConfig.provider.vk.authorizationUri}?" +
                    "client_id=${vkApiConfig.registration.vk.clientId}" +
                    "&client_secret=${vkApiConfig.registration.vk.clientSecret}" +
                    "&redirect_uri=${vkApiConfig.registration.vk.redirectUri}"
        )
        return ResponseEntity.status(HttpStatus.FOUND)
            .location(uri)
            .build()
    }
}