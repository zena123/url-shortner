package com.example.urlshortner.controllers
import com.example.urlshortner.dtos.ShortUrlRequest
import com.example.urlshortner.dtos.ShortUrlResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import com.example.urlshortner.services.UrlShortenerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/v1/urls")
@Tag(name = "URL Shortener", description = "API for shortening URLs")
class UrlShortenerController(
    private val urlShortenerService: UrlShortenerService
)
{
    @Operation(
        summary = "Get URL shortening",
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "URL shortened successfully",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = ShortUrlResponse::class))]
            ),
            ApiResponse(responseCode = "400", description = "Invalid URL format")],
        )
    @PostMapping
    private fun createShortUrl(
        @RequestBody shortUrlRequest: ShortUrlRequest
    ): ResponseEntity<ShortUrlResponse>
    {
        val response = urlShortenerService.createShortUrl(shortUrlRequest)
        val uri = URI.create("/api/v1/urls/${response.shortKey}")
        return ResponseEntity.created(uri).body(response)

    }
    // for demonstration purposes
    @GetMapping("/{shortKey}/meta")
    @Operation(
        summary = "Get original URL metadata",
        description = "Returns the original URL for a given short code without redirection",
        responses = [
            ApiResponse(responseCode = "200", description = "Original URL found"),
            ApiResponse(responseCode = "404", description = "Short URL not found")
        ]
    )
    fun getOriginalUrlMeta(
        @PathVariable shortKey: String
    ): ResponseEntity<Map<String, String>> {
        val originalUrl = urlShortenerService.getLongUrl(shortKey)
        return ResponseEntity.ok(mapOf("originalUrl" to originalUrl))
    }

    @GetMapping("/{shortKey}")
    @Operation(
        summary = "Redirect to original URL",
        description = "Redirects to the original URL using the short code",
        responses = [
            ApiResponse(responseCode = "301", description = "Redirects to original URL"),
            ApiResponse(responseCode = "404", description = "Short URL not found")
        ]
    )
    fun redirectToOriginalUrl(
        @PathVariable shortKey: String
    ): ResponseEntity<Void> {
        val originalUrl = urlShortenerService.getLongUrl(shortKey)
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
            .location(URI.create(originalUrl))
            .build()
    }

}