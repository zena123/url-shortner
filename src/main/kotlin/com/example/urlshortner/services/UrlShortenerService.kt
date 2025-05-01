package com.example.urlshortner.services


import com.example.urlshortner.dtos.ShortUrlRequest
import com.example.urlshortner.dtos.ShortUrlResponse
import com.example.urlshortner.entities.UrlMapping
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service

import com.example.urlshortner.repositories.UrlMappingRepository
import io.seruco.encoding.base62.Base62
import org.springframework.transaction.annotation.Transactional
import sonyflake.core.Sonyflake
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.MalformedURLException
import java.net.URL
import java.nio.ByteBuffer


@Service
class UrlShortenerService (
    private val repository: UrlMappingRepository,
    private val base62: Base62,
    private val sonyflake: Sonyflake
){
    private val domain: String = "http://localhost:8080"  //TODO:Configure
    private val log: Logger = LoggerFactory.getLogger(UrlShortenerService::class.java)


    @Transactional
    fun createShortUrl(request: ShortUrlRequest): ShortUrlResponse {
        validateUrl(request.longUrl)
        val existing = repository.findByOriginalUrl(request.longUrl)
        if(existing != null){
            return toResponse(existing)
        }
        val id = sonyflake.nextId()
        val shortKey = generateShortKey(id)
        val mappingurl = UrlMapping(id = id, shortKey = shortKey, originalUrl = request.longUrl)
        return try {
            log.info(mappingurl.toString())
            val savedObj = repository.save(mappingurl)
            log.info("Successfully saved mapping with short URL: ${savedObj.shortKey}")
            toResponse(savedObj)

        }catch (e: DataIntegrityViolationException) {
            log.warn("Possible race condition while saving mapping for URL: ${request.longUrl}", e)
            val fallback = repository.findByOriginalUrl(request.longUrl)
            return if (fallback != null) {
                log.info("Fallback mapping found for URL: ${request.longUrl}")
                toResponse(fallback)
            } else {
                log.error("Fallback failed: no existing mapping found for URL: ${request.longUrl}")
                throw IllegalStateException("Failed to save or recover mapping for URL: ${request.longUrl}")
            }
        }

    }

    private fun validateUrl(url: String){
        try{
            //Unit function..  check docs
            URL(url)
            log.debug("Valid URL: $url")
        }catch (e: MalformedURLException){
            log.error("Malformed URL: $url")
            throw IllegalArgumentException("Invalid URL: $url", e)
        }

    }
    private fun toResponse(mapping: UrlMapping): ShortUrlResponse {
        return ShortUrlResponse(
            shortKey = mapping.shortKey,
            shortUrl = "$domain/${mapping.shortKey}"
        )
    }
    private fun generateShortKey(id: Long): String {
        return try {
            log.debug("Generating shortkey: $id")
            val buffer = ByteBuffer.allocate(8).putLong(id)
            val encoded = base62.encode(buffer.array())
            String(encoded).replaceFirst("^0+".toRegex(), "").take(8)
        } catch (e: Exception) {
            log.error("Error while generating shortkey", e)
            throw RuntimeException("Failed to generate short key for id: $id", e)
        }
    }







}