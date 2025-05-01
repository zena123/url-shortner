package com.example.urlshortner.entities

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "url_mappings")
class UrlMapping(
    @Id
    @Column(name = "id", nullable = false)
    val id: Long,

    @Column(name = "short_key", nullable = false, unique = true, length = 255)
    val shortKey: String,

    @Column(name = "original_url", nullable = false, columnDefinition = "TEXT")
    val originalUrl: String,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now()
)