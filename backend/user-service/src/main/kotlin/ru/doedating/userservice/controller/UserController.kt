package ru.doedating.userservice.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController {

    @GetMapping
    fun getUser(
        @RequestHeader("X-User-Name") username: String?
    ): ResponseEntity<Any> {
        if (username.isNullOrBlank()) {
            return ResponseEntity.badRequest().body(
                mapOf("error" to "Header X-User-Name is required")
            )
        }

        return ResponseEntity.ok(
            mapOf(
                "username" to username,
                "message" to "Hello, $username"
            )
        )
    }
}