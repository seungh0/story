package com.story.platform.core.domain.subscription

import com.story.platform.core.infrastructure.redis.StringRedisRepository
import org.springframework.stereotype.Repository

@Repository
class SubscriptionCountRepositoryImpl(
    private val stringRedisRepository: StringRedisRepository<SubscriptionCountKey, Long>,
) : SubscriptionCountRepository {

    override suspend fun increase(key: SubscriptionCountKey, count: Long): Long {
        return stringRedisRepository.incrBy(key = key, count = count)
    }

    override suspend fun decrease(key: SubscriptionCountKey, count: Long): Long {
        return stringRedisRepository.decrBy(key = key, count = count)
    }

    override suspend fun get(key: SubscriptionCountKey): Long {
        return stringRedisRepository.get(key) ?: 0L
    }

    override suspend fun delete(key: SubscriptionCountKey) {
        stringRedisRepository.del(key)
    }

}
