package com.story.core.support.lock

interface DistributedLockHandler {

    suspend fun runWithLock(
        distributedLock: DistributedLock,
        lockKey: String,
        runnable: suspend () -> Any?,
    ): Any?

}
