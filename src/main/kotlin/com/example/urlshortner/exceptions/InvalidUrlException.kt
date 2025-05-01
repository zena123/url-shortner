package com.example.urlshortner.exceptions

class InvalidUrlException(url: String) : RuntimeException("Invalid URL: $url")
