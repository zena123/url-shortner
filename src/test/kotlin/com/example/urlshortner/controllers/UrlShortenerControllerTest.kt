package com.example.urlshortner.controllers


import com.example.urlshortner.dtos.ShortUrlRequest
import com.example.urlshortner.dtos.ShortUrlResponse
import com.example.urlshortner.services.UrlShortenerService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.net.URI
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class UrlShortenerControllerTest {

    @MockK
    private lateinit var urlShortenerService: UrlShortenerService

    @InjectMockKs
    private lateinit var controller: UrlShortenerController

    private val testUrl = "https://example.com"
    private val testShortKey = "abc123"
    private val testShortUrl = "http://localhost:8080/$testShortKey"

    @Test
    fun `createShortUrl should return created response`() {

        val request = ShortUrlRequest(testUrl)
        val response = ShortUrlResponse(testShortKey, testShortUrl)

        every { urlShortenerService.createShortUrl(request) } returns response


        val result = controller.createShortUrl(request)


        assertEquals(HttpStatus.CREATED, result.statusCode)
        assertEquals(URI.create("/api/v1/urls/$testShortKey"), result.headers.location)
        assertEquals(response, result.body)
    }

    @Test
    fun `getOriginalUrlMeta should return URL in response`() {

        every { urlShortenerService.getLongUrl(testShortKey) } returns testUrl

        val result = controller.getOriginalUrlMeta(testShortKey)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(mapOf("originalUrl" to testUrl), result.body)
        verify { urlShortenerService.getLongUrl(testShortKey) }
    }

    @Test
    fun `redirectToOriginalUrl should return redirect response`() {
        every { urlShortenerService.getLongUrl(testShortKey) } returns testUrl


        val result = controller.redirectToOriginalUrl(testShortKey)


        assertEquals(HttpStatus.MOVED_PERMANENTLY, result.statusCode)
        assertEquals(URI.create(testUrl), result.headers.location)
    }
}