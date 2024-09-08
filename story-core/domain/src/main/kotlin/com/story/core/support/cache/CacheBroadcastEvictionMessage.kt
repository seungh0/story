package com.story.core.support.cache

internal data class CacheBroadcastEvictionMessage(
    val cacheType: CacheType,
    val cacheKey: String,
    val cacheStrategies: Set<CacheStrategy>,
    val allEntries: Boolean,
) {

    companion object {
        fun allEntries(cacheType: CacheType, cacheStrategies: Set<CacheStrategy>) = CacheBroadcastEvictionMessage(
            cacheType = cacheType,
            cacheKey = "",
            cacheStrategies = cacheStrategies,
            allEntries = true,
        )

        fun targetKey(
            cacheType: CacheType,
            cacheStrategies: Set<CacheStrategy>,
            cacheKey: String,
        ) = CacheBroadcastEvictionMessage(
            cacheType = cacheType,
            cacheKey = cacheKey,
            cacheStrategies = cacheStrategies,
            allEntries = false,
        )
    }

}
