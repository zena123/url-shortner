package com.example.urlshortner.dtos

import io.swagger.v3.oas.annotations.media.Schema


data class ShortUrlResponse(
    @Schema(example = "abc123")
    val shortKey: String,

    @Schema(example = "https://host/abc123")
    val shortUrl: String
)