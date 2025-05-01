package com.example.urlshortner.services


import org.springframework.stereotype.Service

import com.example.urlshortner.repositories.UrlMappingRepository
import io.seruco.encoding.base62.Base62

@Service
class UrlShortenerService (
    private val repository: UrlMappingRepository,
    private val base62: Base62
){
    private val domain: String = "http://localhost:8080"  //TODO:Configure



}