package com.story.platform.core.domain.subscription

import com.story.platform.core.infrastructure.redis.StringRedisRepository
import org.springframework.stereotype.Repository

@Repository
class SubscribersCountRepositoryImpl(
    private val stringRedisRepository: StringRedisRepository<SubscriberCountKey, Long>,
) : SubscribersCountRepository {

    override suspend fun increase(key: SubscriberCountKey, count: Long): Long {
        return stringRedisRepository.incrBy(key = key, count = count)
    }

    override suspend fun decrease(key: SubscriberCountKey, count: Long): Long {
        return stringRedisRepository.decrBy(key = key, count = count)
    }

    override suspend fun get(key: SubscriberCountKey): Long {
        return stringRedisRepository.get(key) ?: 0L
    }

    override suspend fun delete(key: SubscriberCountKey) {
        return stringRedisRepository.del(key)
    }

}
