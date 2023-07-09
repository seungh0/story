package com.story.platform.core.support.cache

import com.story.platform.core.common.error.NotSupportedException
import com.story.platform.core.support.spring.SpringBeanProvider
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import java.util.EnumMap

@Component
class CacheStrategyDelegator(
    private val springBeanProvider: SpringBeanProvider,
) {

    suspend fun getCache(
        cacheStrategyType: CacheStrategyType,
        cacheType: CacheType,
        cacheKey: String,
    ): Any? {
        val cacheStrategy = findCacheStrategy(cacheStrategyType)
        return cacheStrategy.getCache(cacheType = cacheType, cacheKey = cacheKey)
    }

    suspend fun refreshCache(
        cacheStrategyType: CacheStrategyType,
        cacheType: CacheType,
        cacheKey: String,
        value: Any,
    ) {
        val cacheStrategy = findCacheStrategy(cacheStrategyType)
        cacheStrategy.refresh(cacheType = cacheType, cacheKey = cacheKey, value = value)
    }

    suspend fun evict(
        cacheStrategyType: CacheStrategyType,
        cacheType: CacheType,
        cacheKey: String,
    ) {
        val cacheStrategy = findCacheStrategy(cacheStrategyType)
        cacheStrategy.evict(cacheType = cacheType, cacheKey = cacheKey)
    }

    private fun findCacheStrategy(cacheStrategyType: CacheStrategyType): CacheStrategy {
        return cacheStrategyEnumMap[cacheStrategyType]
            ?: throw NotSupportedException("지원하지 않는 캐시 전략($cacheStrategyType) 입니다")
    }

    suspend fun evictAll(cacheStrategyType: CacheStrategyType, cacheType: CacheType) {
        val cacheStrategy = findCacheStrategy(cacheStrategyType)
        cacheStrategy.evictAll(cacheType = cacheType)
    }

    @PostConstruct
    fun initialize() {
        cacheStrategyEnumMap.putAll(
            springBeanProvider.convertBeanMap(
                CacheStrategy::class.java,
                CacheStrategy::cacheStrategy,
            )
        )
    }

    companion object {
        private val cacheStrategyEnumMap = EnumMap<CacheStrategyType, CacheStrategy>(CacheStrategyType::class.java)
    }

}
