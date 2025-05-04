package com.example.urlshortner.services


import com.example.urlshortner.dtos.ShortUrlRequest
import com.example.urlshortner.entities.UrlMapping
import com.example.urlshortner.exceptions.InvalidUrlException
import com.example.urlshortner.exceptions.UrlNotFoundException
import com.example.urlshortner.repositories.UrlMappingRepository
import io.mockk.every
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
import org.apache.commons.validator.routines.UrlValidator

@ExtendWith(MockKExtension::class)
class UrlShortenerServiceTest {

    @MockK
    private lateinit var repository: UrlMappingRepository

    @MockK
    private lateinit var base62: Base62

    @MockK
    private lateinit var sonyflake: Sonyflake

    @MockK
    private lateinit var urlValidator: UrlValidator
    private lateinit var service: UrlShortenerService

    private val testUrl = "https://example.com"
    private val testShortKey = "abc123"
    private val testId = 12345L
    private val testDomain = "https://short.ly"

    @BeforeEach
    fun setup() {
        every { sonyflake.nextId() } returns testId
        every { base62.encode(any<ByteArray>()) } returns testShortKey.toByteArray()
        service = UrlShortenerService(repository, base62, sonyflake, testDomain)
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

    @Test
    fun `createShortUrl should handle race condition gracefully`() {
        val request = ShortUrlRequest(testUrl)
        val savedMapping = UrlMapping(id = testId, shortKey = testShortKey, originalUrl = testUrl)

        // Simulate race: First save() fails, then findByOriginalUrl succeeds
        every { repository.findByOriginalUrl(testUrl) } returns null andThen savedMapping
        every { repository.save(any()) } throws DataIntegrityViolationException("Duplicate key")

        val result = service.createShortUrl(request)

        assertEquals(testShortKey, result.shortKey)
        verify(exactly = 2) { repository.findByOriginalUrl(testUrl) }
    }

    @Test
    fun `createShortUrl should throw error after race condition fails`() {
        every { repository.findByOriginalUrl(testUrl) } returns null
        every { repository.save(any()) } throws DataIntegrityViolationException("Duplicate key")

        assertThrows<IllegalStateException> {
            service.createShortUrl(ShortUrlRequest(testUrl))
        }
    }

    @Test
    fun `getLongUrl should return original URL for valid short key`() {
        val mapping = UrlMapping(id = testId, shortKey = testShortKey, originalUrl = testUrl)
        every { repository.findByShortKey(testShortKey) } returns mapping

        val result = service.getLongUrl(testShortKey)

        assertEquals(testUrl, result)
    }

    @Test
    fun `getLongUrl should throw exception for invalid short key`() {
        every { repository.findByShortKey(testShortKey) } returns null

        assertThrows<UrlNotFoundException> {
            service.getLongUrl(testShortKey)
        }
    }

    @Test
    fun `validateUrl should not throw for a valid URL`() {
        every { urlValidator.isValid(testUrl) } returns true

        assertDoesNotThrow {
            service.validateUrl(testUrl)
        }
    }

    @Test
    fun `validateUrl should throw InvalidUrlException for an invalid URL`() {
        val invalidUrl = "ht!tp://bad_url"
        val exception = assertThrows(InvalidUrlException::class.java) {
            service.validateUrl(invalidUrl)
        }
        assertEquals("Invalid URL: $invalidUrl", exception.message)
    }

    @Test
    fun `generateShortKey should produce valid Base62 output`() {

        val result = service.generateShortKey(testId)

        assertTrue(result.length <= 8)
        assertFalse(result.startsWith("0"))
        assertTrue(result.matches(Regex("[a-zA-Z0-9]+")))
    }

    @Test
    fun `generateShortKey should handle encoding errors`() {
        every { base62.encode(any()) } throws RuntimeException("Encoding failed")

        assertThrows<RuntimeException> {
            service.generateShortKey(testId)
        }.apply {
            assertEquals("Failed to generate short key for id: $testId", message)
        }
    }
}