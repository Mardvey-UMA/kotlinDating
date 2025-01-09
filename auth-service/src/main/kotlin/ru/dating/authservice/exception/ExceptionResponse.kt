package ru.dating.authservice.exception

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class ExceptionResponse(
    var businessErrorCode: Int? = null,
    var businessErrorDescription : String? = null,
    var error : String? = null,
    var validationErrors: Set<String>? = null,
    var errors: Map<String, String>? = null,
)
