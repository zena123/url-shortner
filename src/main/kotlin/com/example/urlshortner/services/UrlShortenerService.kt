package com.example.urlshortner.services

import com.example.urlshortner.dtos.ShortUrlRequest
import com.example.urlshortner.dtos.ShortUrlResponse

interface UrlShortenerService {
    /**
     * Creates a short URL for the given long URL
     * @param request containing the long URL to be shortened
     * @return ShortUrlResponse containing the short key and full short URL
     * @throws com.example.urlshortner.exceptions.InvalidUrlException if the provided URL is invalid
     */
    fun createShortUrl(request: ShortUrlRequest): ShortUrlResponse

    /**
     * Retrieves the original long URL for the given short key
     * @param shortKey the short key to look up
     * @return the original long URL
     * @throws com.example.urlshortner.exceptions.UrlNotFoundException if no mapping exists for the short key
     */
    fun getLongUrl(shortKey: String): String
}