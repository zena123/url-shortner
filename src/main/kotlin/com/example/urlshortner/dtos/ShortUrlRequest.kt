package com.example.urlshortner.dtos

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class ShortUrlRequest(
    @field:NotBlank
    @field:Pattern(regexp = "^(https?|ftp)://[a-zA-Z0-9-_.]+(?:\\.[a-zA-Z0-9-_.]+)+(:\\d+)?(/.*)?$",)
    @Schema(example = "https://example.com/long-path")
    val longUrl: String
)