package com.story.platform.core.domain.subscription

import com.story.platform.core.infrastructure.redis.StringRedisRepository
import org.springframework.stereotype.Repository

@Repository
class SubscribersCountRepositoryImpl(
    private val stringRedisRepository: StringRedisRepository<SubscribersCountKey, Long>,
) : SubscribersCountRepository {

    override suspend fun increase(key: SubscribersCountKey, count: Long): Long {
        return stringRedisRepository.incrBy(key = key, count = count)
    }

    override suspend fun decrease(key: SubscribersCountKey, count: Long): Long {
        return stringRedisRepository.decrBy(key = key, count = count)
    }

    override suspend fun get(key: SubscribersCountKey): Long {
        return stringRedisRepository.get(key) ?: 0L
    }

    override suspend fun delete(key: SubscribersCountKey) {
        return stringRedisRepository.del(key)
    }

}
