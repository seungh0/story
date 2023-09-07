package com.story.platform.core.domain.reaction

import com.story.platform.core.infrastructure.redis.StringRedisRepository
import org.springframework.stereotype.Repository

@Repository
class ReactionCountRepositoryImpl(
    private val stringRedisRepository: StringRedisRepository<ReactionCountKey, Long>,
) : ReactionCountRepository {

    override suspend fun increase(key: ReactionCountKey, count: Long): Long {
        return stringRedisRepository.incrBy(key = key, count = count)
    }

    override suspend fun increaseBulk(
        keys: Set<ReactionCountKey>,
        count: Long,
    ): Map<ReactionCountKey, Long> = stringRedisRepository.incrBulkBy(keys = keys, count = count)

    override suspend fun decrease(key: ReactionCountKey, count: Long): Long {
        return stringRedisRepository.decrBy(key = key, count = count)
    }

    override suspend fun decreaseBulk(
        keys: Set<ReactionCountKey>,
        count: Long,
    ): Map<ReactionCountKey, Long> = stringRedisRepository.decrBulkBy(keys = keys, count = count)

    override suspend fun get(key: ReactionCountKey): Long {
        return stringRedisRepository.get(key = key) ?: 0L
    }

    override suspend fun getBulk(keys: Set<ReactionCountKey>): Map<ReactionCountKey, Long> {
        return stringRedisRepository.getBulkMap(keys.toList()).map { (key, value) -> key to (value ?: 0L) }.toMap()
    }

    override suspend fun delete(key: ReactionCountKey) {
        stringRedisRepository.del(key = key)
    }

    override suspend fun deleteBulk(keys: Set<ReactionCountKey>) {
        stringRedisRepository.delBulk(keys = keys)
    }

}
