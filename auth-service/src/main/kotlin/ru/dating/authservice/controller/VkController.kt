package ru.dating.authservice.controller

import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.dating.authservice.dto.AuthResponseDTO
import ru.dating.authservice.service.OAuthService
@Tag(name = "User7")
@RestController
@RequestMapping("/api/auth/login")
class VkController(
    private val oAuthService: OAuthService
){
    //@Hidden
    @GetMapping("/oauth2/code/vk")
    fun handleRedirect(
        @RequestParam("code") code: String,
        response: HttpServletResponse
    ): AuthResponseDTO = oAuthService.authenticate(code, response)

    // Чисто открыть страничку VK авторизации
    @GetMapping("/vk")
    fun authorizeVK(request: HttpServletRequest): ResponseEntity<String>  = oAuthService.vkLoginPageOpen()
}
