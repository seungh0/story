package com.story.platform.core.infrastructure.cache

import com.story.platform.core.common.error.NotSupportedException
import com.story.platform.core.infrastructure.spring.SpringBeanProvider
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import java.util.EnumMap

@Component
class CacheHandlerDelegator(
    private val springBeanProvider: SpringBeanProvider,
) {

    suspend fun getCache(
        cacheStrategy: CacheStrategy,
        cacheType: CacheType,
        cacheKey: String,
    ): Any? {
        val handler = findHandler(cacheStrategy)
        return handler.getCache(cacheType = cacheType, cacheKey = cacheKey)
    }

    suspend fun refreshCache(
        cacheStrategy: CacheStrategy,
        cacheType: CacheType,
        cacheKey: String,
        value: Any,
    ) {
        val handler = findHandler(cacheStrategy)
        handler.refresh(cacheType = cacheType, cacheKey = cacheKey, value = value)
    }

    suspend fun evict(
        cacheStrategy: CacheStrategy,
        cacheType: CacheType,
        cacheKey: String,
    ) {
        val handler = findHandler(cacheStrategy)
        handler.evict(cacheType = cacheType, cacheKey = cacheKey)
    }

    suspend fun evictAll(cacheStrategy: CacheStrategy, cacheType: CacheType) {
        val handler = findHandler(cacheStrategy)
        handler.evictAll(cacheType = cacheType)
    }

    private fun findHandler(cacheStrategy: CacheStrategy): CacheHandler {
        return cacheHandlerEnumMap[cacheStrategy]
            ?: throw NotSupportedException("지원하지 않는 캐시 전략($cacheStrategy) 입니다")
    }

    @PostConstruct
    fun initialize() {
        cacheHandlerEnumMap.putAll(
            springBeanProvider.convertBeanMap(
                CacheHandler::class.java,
                CacheHandler::cacheStrategy,
            )
        )
    }

    companion object {
        private val cacheHandlerEnumMap = EnumMap<CacheStrategy, CacheHandler>(CacheStrategy::class.java)
    }

}
