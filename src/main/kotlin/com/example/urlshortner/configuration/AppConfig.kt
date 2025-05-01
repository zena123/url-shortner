package com.example.urlshortner.configuration

import io.seruco.encoding.base62.Base62
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfig {

    @Bean
    fun base62(): Base62 = Base62.createInstance()

}
