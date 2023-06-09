package com.story.platform.core.domain.subscription

import com.story.platform.core.infrastructure.redis.StringRedisRepository
import org.springframework.stereotype.Repository

@Repository
class SubscriptionsCountRepositoryImpl(
    private val stringRedisRepository: StringRedisRepository<SubscriptionsCountKey, Long>,
) : SubscriptionsCountRepository {

    override suspend fun increase(key: SubscriptionsCountKey, count: Long): Long {
        return stringRedisRepository.incrBy(key = key, count = count)
    }

    override suspend fun decrease(key: SubscriptionsCountKey, count: Long): Long {
        return stringRedisRepository.decrBy(key = key, count = count)
    }

    override suspend fun get(key: SubscriptionsCountKey): Long {
        return stringRedisRepository.get(key) ?: 0L
    }

    override suspend fun delete(key: SubscriptionsCountKey) {
        stringRedisRepository.del(key)
    }

}
