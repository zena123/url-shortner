package com.example.urlshortner.dtos

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
data class ShortUrlRequest(
    @field:NotBlank
    @Schema(example = "https://example.com/long-path")
    val longUrl: String
)