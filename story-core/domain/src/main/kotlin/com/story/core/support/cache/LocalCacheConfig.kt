package com.story.core.support.cache

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@EnableCaching
@Configuration
class LocalCacheConfig {

    @Bean(CAFFEINE_CACHE_MANAGER)
    fun caffeineCacheManager(): CacheManager {
        val simpleCacheManager = SimpleCacheManager()
        simpleCacheManager.setCaches(caffeineCaches())
        return simpleCacheManager
    }

    private fun caffeineCaches(): List<CaffeineCache> {
        return CacheType.LOCAL_CACHE_TYPES
            .map { cache ->
                CaffeineCache(
                    cache.key,
                    Caffeine.newBuilder()
                        .expireAfterWrite(cache.localCacheTtl!!)
                        .build()
                )
            }
    }

    companion object {
        const val CAFFEINE_CACHE_MANAGER = "caffeineCacheManager"
    }

}
