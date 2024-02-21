package com.story.core.infrastructure.lock

import io.netty.util.internal.ThreadLocalRandom
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.redisson.api.RedissonReactiveClient
import org.springframework.stereotype.Service

@Service
class RedissonDistributedLockHandler(
    private val redissonReactiveClient: RedissonReactiveClient,
) : DistributedLockHandler {

    override suspend fun executeInCriticalSection(
        distributedLock: DistributedLock,
        lockKey: String,
        runnable: () -> Any?,
    ): Any? {
        val redisLock = redissonReactiveClient.getLock(lockKey)
        val threadId = ThreadLocalRandom.current().nextLong()

        val acquired = redisLock.tryLock(
            distributedLock.waitTime,
            distributedLock.leaseTime,
            distributedLock.timeUnit,
            threadId
        ).awaitSingle()

        if (!acquired) {
            throw IllegalStateException("분산 락($lockKey)을 획득하는데 실패하였습니다.")
        }

        return try {
            runnable.invoke()
        } finally {
            redisLock.unlock(threadId).awaitSingleOrNull()
        }
    }

}
