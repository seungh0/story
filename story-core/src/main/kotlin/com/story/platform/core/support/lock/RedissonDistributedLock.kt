package com.story.platform.core.support.lock

import com.story.platform.core.common.error.InternalServerException
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service

@Service
class RedissonDistributedLock(
    private val redissonClient: RedissonClient,
) {

    suspend fun executeInCriticalSection(
        distributedLock: DistributedLock,
        lockKey: String,
        runnable: () -> Any?,
    ): Any? {
        val redisLock = redissonClient.getLock(lockKey)

        val acquired = redisLock.tryLock(distributedLock.waitTime, distributedLock.leaseTime, distributedLock.timeUnit)
        if (!acquired) {
            throw InternalServerException("분산 락($lockKey)을 선점하는데 실패하였습니다. [lockType: ${distributedLock.lockType} lockKey: $lockKey]")
        }

        return try {
            runnable.invoke()
        } finally {
            redisLock.unlock()
        }
    }

}
