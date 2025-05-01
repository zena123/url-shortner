package com.example.urlshortner.repositories

import com.example.urlshortner.entities.UrlMapping
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository


@Repository
interface UrlMappingRepository : JpaRepository<UrlMapping, Long> {
    fun findByShortKey(shortKey: String): UrlMapping?

    fun findByOriginalUrl(originalUrl: String): UrlMapping?

}