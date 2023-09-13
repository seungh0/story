package com.story.platform.core.infrastructure.cache

enum class CacheStrategy(
    private val description: String,
) {

    LOCAL(description = "로컬 캐시"),
    GLOBAL(description = "글로벌 캐시"),

}
