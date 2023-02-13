package com.story.platform.core.support.cache

enum class CacheStrategyType(
    private val description: String,
) {

    LOCAL(description = "로컬 캐시"),
    GLOBAL(description = "글로벌 캐시"),

}
