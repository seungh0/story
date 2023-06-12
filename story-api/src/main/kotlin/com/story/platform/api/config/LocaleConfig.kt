package com.story.platform.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.server.i18n.AcceptHeaderLocaleContextResolver
import org.springframework.web.server.i18n.LocaleContextResolver
import java.util.Locale

@Configuration
class LocaleConfig {

    @Bean
    fun localeResolver(): LocaleContextResolver = AcceptHeaderLocaleContextResolver().apply {
        defaultLocale = Locale.US
        supportedLocales = listOf(Locale.KOREA, Locale.US)
    }

}
