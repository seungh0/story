package com.story.core.infrastructure.photo

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("story.photo")
data class PhotoProperties(
    val domain: String,
)
