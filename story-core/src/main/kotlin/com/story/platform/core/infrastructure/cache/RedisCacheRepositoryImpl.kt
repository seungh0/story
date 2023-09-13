package com.story.platform.core.infrastructure.cache

import com.story.platform.core.common.error.NotSupportedException
import com.story.platform.core.infrastructure.redis.StringRedisRepository
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class RedisCacheRepositoryImpl(
    private val stringRedisRepository: StringRedisRepository<RedisCacheKey, String>,
) : RedisCacheRepository {

    override suspend fun getCache(cacheType: CacheType, cacheKey: String): String? {
        if (!cacheType.enableGlobalCache()) {
            throw NotSupportedException("레디스 캐시를 지원하지 않는 캐시($cacheType) 입니다")
        }
        val redisCacheKey = RedisCacheKey.of(cacheType = cacheType, cacheKey = cacheKey)
        return stringRedisRepository.get(redisCacheKey)
    }

    override suspend fun setCache(cacheType: CacheType, cacheKey: String, value: String) {
        if (!cacheType.enableGlobalCache()) {
            throw NotSupportedException("레디스 캐시를 지원하지 않는 캐시($cacheType) 입니다")
        }
        val redisCacheKey = RedisCacheKey.of(cacheType = cacheType, cacheKey = cacheKey)
        stringRedisRepository.setWithTtl(redisCacheKey, value, cacheType.globalCacheTtl)
    }

    override suspend fun evict(cacheType: CacheType, cacheKey: String) {
        if (!cacheType.enableGlobalCache()) {
            throw NotSupportedException("레디스 캐시를 지원하지 않는 캐시($cacheType) 입니다")
        }
        val redisCacheKey = RedisCacheKey.of(cacheType = cacheType, cacheKey = cacheKey)
        stringRedisRepository.del(redisCacheKey)
    }

    override suspend fun isEarlyRecomputedRequired(
        cacheType: CacheType,
        cacheKey: String,
    ): Boolean {
        val totalExpiredTtl: Duration = cacheType.globalCacheTtl
            ?: throw NotSupportedException("레디스 캐시를 지원하지 않는 캐시($cacheType) 입니다")
        val currentExpiredTtl = getTtl(cacheType = cacheType, cacheKey = cacheKey)
        return CacheProbabilisticUtils.isEarlyRecomputeRequired(
            currentTtl = currentExpiredTtl,
            expiryGap = Duration.ofMillis(totalExpiredTtl.toMillis() / 10)
        )
    }

    override suspend fun getTtl(cacheType: CacheType, cacheKey: String): Duration {
        if (!cacheType.enableGlobalCache()) {
            throw NotSupportedException("레디스 캐시를 지원하지 않는 캐시($cacheType) 입니다")
        }
        val redisCacheKey = RedisCacheKey.of(cacheType = cacheType, cacheKey = cacheKey)
        return stringRedisRepository.getTtl(redisCacheKey)
    }

    override suspend fun evictAll(cacheType: CacheType) {
        if (!cacheType.enableGlobalCache()) {
            throw NotSupportedException("레디스 캐시를 지원하지 않는 캐시($cacheType) 입니다")
        }
        val keyPrefix = RedisCacheKey.getCachePrefix(cacheType)
        val keyStrings = stringRedisRepository.scan(keyPrefix)
        if (keyStrings.isEmpty()) {
            return
        }

        val cacheKeys =
            keyStrings.map { keyString -> RedisCacheKey.fromKeyString(cacheType = cacheType, keyString = keyString) }
        stringRedisRepository.delBulk(cacheKeys.toSet())
    }

}
