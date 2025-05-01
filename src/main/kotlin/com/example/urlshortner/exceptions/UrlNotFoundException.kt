package com.example.urlshortner.exceptions


class UrlNotFoundException(shortKey: String) : RuntimeException("URL not found for shortKey: $shortKey")