package com.story.core.support.cache

import com.story.core.common.error.NotSupportedException
import com.story.core.support.redis.StringRedisRepository
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class GlobalCacheRepository(
    private val stringRedisRepository: StringRedisRepository<GlobalCacheKey, String>,
) {

    suspend fun getCache(cacheType: CacheType, cacheKey: String): String? {
        if (!cacheType.enableGlobalCache()) {
            throw NotSupportedException("레디스 캐시를 지원하지 않는 캐시($cacheType) 입니다")
        }
        val globalCacheKey = GlobalCacheKey.of(cacheType = cacheType, cacheKey = cacheKey)
        return stringRedisRepository.get(globalCacheKey)
    }

    suspend fun setCache(cacheType: CacheType, cacheKey: String, value: String) {
        if (!cacheType.enableGlobalCache()) {
            throw NotSupportedException("레디스 캐시를 지원하지 않는 캐시($cacheType) 입니다")
        }
        val globalCacheKey = GlobalCacheKey.of(cacheType = cacheType, cacheKey = cacheKey)
        stringRedisRepository.setWithTtl(globalCacheKey, value, cacheType.globalCacheTtl)
    }

    suspend fun evict(cacheType: CacheType, cacheKey: String) {
        if (!cacheType.enableGlobalCache()) {
            throw NotSupportedException("레디스 캐시를 지원하지 않는 캐시($cacheType) 입니다")
        }
        val globalCacheKey = GlobalCacheKey.of(cacheType = cacheType, cacheKey = cacheKey)
        stringRedisRepository.del(globalCacheKey)
    }

    suspend fun isEarlyRecomputedRequired(
        cacheType: CacheType,
        cacheKey: String,
    ): Boolean {
        val totalExpiredTtl: Duration = cacheType.globalCacheTtl
            ?: throw NotSupportedException("레디스 캐시를 지원하지 않는 캐시($cacheType) 입니다")
        val currentExpiredTtl = getTtl(cacheType = cacheType, cacheKey = cacheKey)

        if (currentExpiredTtl <= Duration.ZERO) {
            return true
        }

        return CacheProbabilisticUtils.isEarlyRecomputeRequired(
            currentTtl = currentExpiredTtl,
            expiryGap = Duration.ofMillis(totalExpiredTtl.toMillis() / 10)
        )
    }

    suspend fun getTtl(cacheType: CacheType, cacheKey: String): Duration {
        if (!cacheType.enableGlobalCache()) {
            throw NotSupportedException("레디스 캐시를 지원하지 않는 캐시($cacheType) 입니다")
        }
        val globalCacheKey = GlobalCacheKey.of(cacheType = cacheType, cacheKey = cacheKey)
        return stringRedisRepository.getTtl(globalCacheKey)
    }

    suspend fun evictAll(cacheType: CacheType) {
        if (!cacheType.enableGlobalCache()) {
            throw NotSupportedException("레디스 캐시를 지원하지 않는 캐시($cacheType) 입니다")
        }
        val keyPrefix = GlobalCacheKey.getCachePrefix(cacheType)
        val keyStrings = stringRedisRepository.scan(keyPrefix)
        if (keyStrings.isEmpty()) {
            return
        }

        val cacheKeys =
            keyStrings.map { keyString -> GlobalCacheKey.fromKeyString(cacheType = cacheType, keyString = keyString) }
        stringRedisRepository.delBulk(cacheKeys.toSet())
    }

}
