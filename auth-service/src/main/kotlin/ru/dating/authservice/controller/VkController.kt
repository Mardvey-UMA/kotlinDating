package ru.dating.authservice.controller

import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.dating.authservice.dto.AuthResponseDTO
import ru.dating.authservice.service.OAuthService

@RestController
@RequestMapping("/api/auth")
class VkController (
    private val oAuthService: OAuthService
){
    @GetMapping("/login/oauth2/code/vk")
    fun handleRedirect(
        @RequestParam("code") code: String,
        response: HttpServletResponse
    ): AuthResponseDTO {
        //val authResponse =
        return oAuthService.authenticate(code, response)
    }
    @GetMapping("/oauth2/vk")
    fun oauth2(
        @RegisteredOAuth2AuthorizedClient("vk") authorizedClient: OAuth2AuthorizedClient
    ): Unit {}
}
