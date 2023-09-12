package com.story.platform.core.domain.reaction

import com.story.platform.core.infrastructure.redis.StringRedisRepository
import org.springframework.stereotype.Repository

@Repository
class ReactionCountRepository(
    private val stringRedisRepository: StringRedisRepository<ReactionCountKey, Long>,
) {

    suspend fun increase(key: ReactionCountKey, count: Long = 1L): Long {
        return stringRedisRepository.incrBy(key = key, count = count)
    }

    suspend fun decrease(key: ReactionCountKey, count: Long): Long {
        return stringRedisRepository.decrBy(key = key, count = count)
    }

    suspend fun get(key: ReactionCountKey): Long {
        return stringRedisRepository.get(key = key) ?: 0L
    }

    suspend fun getBulk(keys: Set<ReactionCountKey>): Map<ReactionCountKey, Long> {
        return stringRedisRepository.getBulkMap(keys.toList()).map { (key, value) -> key to (value ?: 0L) }.toMap()
    }

    suspend fun delete(key: ReactionCountKey) {
        stringRedisRepository.del(key = key)
    }

    suspend fun deleteBulk(keys: Set<ReactionCountKey>) {
        stringRedisRepository.delBulk(keys = keys)
    }

}
