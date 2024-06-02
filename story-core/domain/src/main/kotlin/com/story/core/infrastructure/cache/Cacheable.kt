package com.story.core.infrastructure.cache

import org.intellij.lang.annotations.Language

@Target(AnnotationTarget.FUNCTION)
annotation class Cacheable(
    val cacheType: CacheType,

    @Language(value = "SpEL")
    val key: String,

    @Language(value = "SpEL")
    val condition: String = "",

    @Language(value = "SpEL")
    val unless: String = "",
)
