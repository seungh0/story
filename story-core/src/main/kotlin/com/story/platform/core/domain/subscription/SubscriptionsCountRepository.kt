package com.story.platform.core.domain.subscription

import com.story.platform.core.infrastructure.redis.StringRedisRepository
import org.springframework.stereotype.Repository

@Repository
class SubscriptionsCountRepository(
    private val stringRedisRepository: StringRedisRepository<SubscriptionsCountKey, Long>,
) {

    suspend fun increase(key: SubscriptionsCountKey, count: Long = 1L): Long {
        return stringRedisRepository.incrBy(key = key, count = count)
    }

    suspend fun decrease(key: SubscriptionsCountKey, count: Long = 1L): Long {
        return stringRedisRepository.decrBy(key = key, count = count)
    }

    suspend fun get(key: SubscriptionsCountKey): Long {
        return stringRedisRepository.get(key) ?: 0L
    }

    suspend fun delete(key: SubscriptionsCountKey) {
        stringRedisRepository.del(key)
    }

}
