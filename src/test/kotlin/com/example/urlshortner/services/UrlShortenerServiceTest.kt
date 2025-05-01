package com.example.urlshortner.services


import com.example.urlshortner.dtos.ShortUrlRequest
import com.example.urlshortner.dtos.ShortUrlResponse
import com.example.urlshortner.entities.UrlMapping
import com.example.urlshortner.exceptions.InvalidUrlException
import com.example.urlshortner.exceptions.UrlNotFoundException
import com.example.urlshortner.repositories.UrlMappingRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import io.seruco.encoding.base62.Base62
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.assertThrows
import org.springframework.dao.DataIntegrityViolationException
import sonyflake.core.Sonyflake
import java.net.MalformedURLException
import java.net.URL
import java.nio.ByteBuffer

@ExtendWith(MockKExtension::class)
class UrlShortenerServiceTest {

    @MockK
    private lateinit var repository: UrlMappingRepository

    @MockK
    private lateinit var base62: Base62

    @MockK
    private lateinit var sonyflake: Sonyflake

    @InjectMockKs
    private lateinit var service: UrlShortenerService

    private val testUrl = "https://example.com"
    private val testShortKey = "abc123"
    private val testId = 12345L

    @BeforeEach
    fun setup() {
        every { sonyflake.nextId() } returns testId
        every { base62.encode(any<ByteArray>()) } returns testShortKey.toByteArray()
    }

    @Test
    fun `createShortUrl should return existing mapping if URL exists`() {
        val request = ShortUrlRequest(testUrl)
        val existingMapping = UrlMapping(id = testId, shortKey = testShortKey, originalUrl = testUrl)

        every { repository.findByOriginalUrl(testUrl) } returns existingMapping

        val result = service.createShortUrl(request)

        assertEquals(testShortKey, result.shortKey)
    }

    @Test
    fun `createShortUrl should create new mapping for new URL`() {
        val request = ShortUrlRequest(testUrl)
        val newMapping = UrlMapping(id = testId, shortKey = testShortKey, originalUrl = testUrl)

        every { repository.findByOriginalUrl(testUrl) } returns null
        every { repository.save(any()) } returns newMapping

        val result = service.createShortUrl(request)

        assertEquals(testShortKey, result.shortKey)
        verify { repository.save(any()) }
    }
}