package com.story.core.infrastructure.lock

interface DistributedLockHandler {

    suspend fun runWithLock(
        distributedLock: DistributedLock,
        lockKey: String,
        runnable: suspend () -> Any?,
    ): Any?

}
