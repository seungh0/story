package com.story.core.support.cache

import com.story.core.common.error.InternalServerException
import com.story.core.common.json.toJson
import com.story.core.common.json.toObject
import com.story.core.common.logger.LoggerExtension.log
import com.story.core.support.circuitbreaker.CircuitBreaker
import com.story.core.support.circuitbreaker.CircuitBreakerType
import com.story.core.support.circuitbreaker.circuit
import com.story.core.support.circuitbreaker.fallbackIfOpen
import org.springframework.stereotype.Repository

@Repository
class GlobalCacheHandler(
    private val globalCacheRepository: GlobalCacheRepository,
    private val circuitBreaker: CircuitBreaker,
) : CacheHandler {

    override fun cacheStrategy(): CacheStrategy = CacheStrategy.GLOBAL

    override suspend fun getCache(cacheType: CacheType, cacheKey: String): Any? {
        if (!cacheType.enableGlobalCache()) {
            return null
        }

        return circuit(circuitBreakerType = CircuitBreakerType.REDIS_CACHE, circuitBreaker = circuitBreaker) {
            if (globalCacheRepository.isEarlyRecomputedRequired(cacheType = cacheType, cacheKey = cacheKey)) {
                return@circuit null
            }

            val redisCacheValueJson = globalCacheRepository.getCache(cacheType = cacheType, cacheKey = cacheKey)
            if (redisCacheValueJson.isNullOrBlank()) {
                return@circuit null
            }

            val globalCacheValue = redisCacheValueJson.toObject(cacheType.typeReference)
            log.debug { "[Cache] 글로벌 캐시로부터 데이터를 가져옵니다 [key:$cacheKey value: $globalCacheValue]" }
            return@circuit globalCacheValue
        }.fallbackIfOpen { throwable ->
            throw InternalServerException(
                message = "[Cache] 글로벌 캐시에 대해서 서킷이 Open 되어있어 fast-fail 합니다",
                cause = throwable
            )
        }.getOrThrow()
    }

    override suspend fun refresh(cacheType: CacheType, cacheKey: String, value: Any) {
        if (!cacheType.enableGlobalCache()) {
            return
        }

        circuit(circuitBreakerType = CircuitBreakerType.REDIS_CACHE, circuitBreaker = circuitBreaker) {
            globalCacheRepository.setCache(cacheType = cacheType, cacheKey = cacheKey, value = value.toJson())
            log.debug { "[Cache] 글로벌 캐시를 갱신합니다 [cacheType: $cacheType keyString: $cacheKey value: $value]" }
        }.fallbackIfOpen { throwable ->
            throw InternalServerException(
                message = "[Cache] 글로벌 캐시에 대해서 서킷이 Open 되어있어 fast-fail 합니다",
                cause = throwable
            )
        }.getOrThrow()
    }

    override suspend fun evict(cacheType: CacheType, cacheKey: String) {
        if (!cacheType.enableGlobalCache()) {
            return
        }

        circuit(circuitBreakerType = CircuitBreakerType.REDIS_CACHE, circuitBreaker = circuitBreaker) {
            globalCacheRepository.evict(cacheType = cacheType, cacheKey = cacheKey)
            log.debug { "[Cache] 글로벌 캐시를 삭제합니다 [cacheType: $cacheType keyString: $cacheKey]" }
        }.fallbackIfOpen { throwable ->
            throw InternalServerException(
                message = "[Cache] 글로벌 캐시에 대해서 서킷이 Open 되어있어 fast-fail 합니다",
                cause = throwable
            )
        }.getOrThrow()
    }

    override suspend fun evictAll(cacheType: CacheType) {
        if (!cacheType.enableGlobalCache()) {
            return
        }

        circuit(circuitBreakerType = CircuitBreakerType.REDIS_CACHE, circuitBreaker = circuitBreaker) {
            globalCacheRepository.evictAll(cacheType = cacheType)
            log.debug { "[Cache] 글로벌 캐시를 전체 삭제합니다 [cacheType: $cacheType]" }
        }.fallbackIfOpen { throwable ->
            throw InternalServerException(
                message = "[Cache] 글로벌 캐시에 대해서 서킷이 Open 되어있어 fast-fail 합니다",
                cause = throwable
            )
        }.getOrThrow()
    }

}
