package com.story.core.infrastructure.lock

import com.story.core.common.logger.LoggerExtension.log
import io.netty.util.internal.ThreadLocalRandom
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.redisson.api.RedissonReactiveClient
import org.springframework.stereotype.Service

@Service
class RedissonDistributedLockHandler(
    private val redissonReactiveClient: RedissonReactiveClient,
) : DistributedLockHandler {

    override suspend fun runWithLock(
        distributedLock: DistributedLock,
        lockKey: String,
        runnable: suspend () -> Any?,
    ): Any? {
        val redisLock = redissonReactiveClient.getLock(lockKey)
        val threadId = ThreadLocalRandom.current().nextLong()

        val acquired = redisLock.tryLock(
            distributedLock.waitTime,
            distributedLock.leaseTime,
            distributedLock.timeUnit,
            threadId
        ).awaitSingle()

        log.info { Thread.currentThread().id }

        if (!acquired) {
            throw IllegalStateException("분산 락($lockKey)을 획득하는데 실패하였습니다.")
        }

        try {
            val invoke = runnable.invoke()
            log.info { Thread.currentThread().id }
            return invoke
        } finally {
            redisLock.unlock(threadId).awaitSingleOrNull()
        }
    }

}
