package com.story.platform.core.infrastructure.cache

import org.intellij.lang.annotations.Language

@Target(AnnotationTarget.FUNCTION)
annotation class CacheEvict(
    val cacheType: CacheType,

    @Language(value = "SpEL")
    val key: String = "",

    val allEntries: Boolean = false,

    val targetCacheStrategies: Array<CacheStrategy> = [CacheStrategy.LOCAL, CacheStrategy.GLOBAL],

    @Language(value = "SpEL")
    val condition: String = "",

    @Language(value = "SpEL")
    val unless: String = "",
)
