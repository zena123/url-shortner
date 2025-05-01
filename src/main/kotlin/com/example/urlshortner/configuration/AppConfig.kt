package com.example.urlshortner.configuration

import io.seruco.encoding.base62.Base62
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import sonyflake.config.SonyflakeSettings
import sonyflake.core.Sonyflake
import java.time.Instant

@Configuration
class AppConfig {

    @Bean
    fun base62(): Base62 = Base62.createInstance()

    @Bean
    fun sonyflake(): Sonyflake {
        val settings = SonyflakeSettings.of(Instant.now())
        return Sonyflake.of(settings)
    }
}
