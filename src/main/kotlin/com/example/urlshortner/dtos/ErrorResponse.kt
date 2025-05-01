package com.example.urlshortner.dtos
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Standard error response returned when an API call fails")
data class ErrorResponse(
    @Schema(description = "HTTP status code of the error", example = "400")
    val status: Int,

    @Schema(description = "Application-specific error code", example = "INVALID_URL")
    val code: String,

    @Schema(description = "Error message", example = "The provided URL is invalid")
    val message: String
)