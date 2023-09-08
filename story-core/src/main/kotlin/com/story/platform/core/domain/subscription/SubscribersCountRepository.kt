package com.story.platform.core.domain.subscription

import com.story.platform.core.infrastructure.redis.StringRedisRepository
import org.springframework.stereotype.Repository

@Repository
class SubscribersCountRepository(
    private val stringRedisRepository: StringRedisRepository<SubscribersCountKey, Long>,
) {

    suspend fun increase(key: SubscribersCountKey, count: Long = 1L): Long {
        return stringRedisRepository.incrBy(key = key, count = count)
    }

    suspend fun decrease(key: SubscribersCountKey, count: Long = 1L): Long {
        return stringRedisRepository.decrBy(key = key, count = count)
    }

    suspend fun get(key: SubscribersCountKey): Long {
        return stringRedisRepository.get(key) ?: 0L
    }

    suspend fun delete(key: SubscribersCountKey) {
        return stringRedisRepository.del(key)
    }

}
