package com.story.platform.core.support.lock

import com.story.platform.core.common.error.ErrorCode
import com.story.platform.core.common.error.InternalServerException
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service

@Service
class DistributedLockExecutor(
    private val redissonClient: RedissonClient,
) {

    fun execute(
        distributeLock: DistributeLock,
        lockKey: String,
        runnable: () -> Any?,
    ): Any? {
        val redisLock: RLock = redissonClient.getLock(lockKey)

        val acquired = redisLock.tryLock(distributeLock.waitTime, distributeLock.leaseTime, distributeLock.timeUnit)
        if (!acquired) {
            throw InternalServerException("분산 락($lockKey)을 획득하는데 실패하였습니다.", ErrorCode.E500_INTERNAL_SERVER_ERROR)
        }

        return try {
            runnable.invoke()
        } finally {
            redisLock.unlock()
        }
    }

}
