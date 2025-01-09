package ru.dating.authservice.enums

import org.springframework.http.HttpStatus

enum class BusinessErrorCodes (
    val code: Int,
    val httpStatus: HttpStatus,
    val description: String
){
    NO_CODE(0, HttpStatus.NOT_IMPLEMENTED, "No code"),
    INCORRECT_CURRENT_PASSWORD(300, HttpStatus.BAD_REQUEST, "Incorrect password"),
    ACCOUNT_LOCKED(302, HttpStatus.FORBIDDEN, "User account is locked"),
    ACCOUNT_DISABLED(303, HttpStatus.FORBIDDEN, "User account is disabled"),
    BAD_CREDENTIALS(304, HttpStatus.FORBIDDEN, "Login and / or password is incorrect"),
    NEW_PASSWORD_DOES_NOT_MATCH(301, HttpStatus.BAD_REQUEST, "The new password does not match"),

}