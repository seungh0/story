package com.story.platform.core.support.cache

import com.story.platform.core.common.error.InternalServerException
import com.story.platform.core.support.redis.StringRedisRepository
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class RedisCacheRepositoryImpl(
    private val stringRedisRepository: StringRedisRepository<RedisCacheKey, String>,
) : RedisCacheRepository {

    override suspend fun getCache(cacheType: CacheType, cacheKey: String): String? {
        if (!cacheType.enableGlobalCache()) {
            throw InternalServerException("해당 캐시($cacheType)는 레디스 캐시를 지원하지 않습니다")
        }
        val redisCacheKey = RedisCacheKey.of(cacheType = cacheType, cacheKey = cacheKey)
        return stringRedisRepository.get(redisCacheKey)
    }

    override suspend fun setCache(cacheType: CacheType, cacheKey: String, value: String) {
        if (!cacheType.enableGlobalCache()) {
            throw InternalServerException("해당 캐시($cacheType)는 레디스 캐시를 지원하지 않습니다")
        }
        val redisCacheKey = RedisCacheKey.of(cacheType = cacheType, cacheKey = cacheKey)
        stringRedisRepository.setWithTtl(redisCacheKey, value, cacheType.globalCacheTtl)
    }

    override suspend fun evict(cacheType: CacheType, cacheKey: String) {
        if (!cacheType.enableGlobalCache()) {
            throw InternalServerException("해당 캐시($cacheType)는 레디스 캐시를 지원하지 않습니다")
        }
        val redisCacheKey = RedisCacheKey.of(cacheType = cacheType, cacheKey = cacheKey)
        stringRedisRepository.del(redisCacheKey)
    }

    override suspend fun isEarlyRecomputedRequired(
        cacheType: CacheType,
        cacheKey: String,
    ): Boolean {
        val totalExpiredTtl: Duration = cacheType.globalCacheTtl
            ?: throw InternalServerException("해당 캐시($cacheType)는 레디스 캐시를 지원하지 않습니다")
        val currentExpiredTtl = getTtl(cacheType = cacheType, cacheKey = cacheKey)
        return CachePERUtils.isEarlyRecomputeRequired(
            currentTtl = currentExpiredTtl,
            expiryGap = Duration.ofMillis(totalExpiredTtl.toMillis() / 10)
        )
    }

    override suspend fun getTtl(cacheType: CacheType, cacheKey: String): Duration {
        if (!cacheType.enableGlobalCache()) {
            throw InternalServerException("해당 캐시($cacheType)는 레디스 캐시를 지원하지 않습니다")
        }
        val redisCacheKey = RedisCacheKey.of(cacheType = cacheType, cacheKey = cacheKey)
        return stringRedisRepository.getTtl(redisCacheKey)
    }

}
