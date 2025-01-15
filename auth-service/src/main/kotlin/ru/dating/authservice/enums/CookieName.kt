package ru.dating.authservice.enums

enum class CookieName(
    private val cookieName: String,
) {
    ACCESS_TOKEN("access_token"),
    REFRESH_TOKEN("refresh_token"),
}