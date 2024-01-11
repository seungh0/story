package com.story.core.infrastructure.lock

interface DistributedLockHandler {

    suspend fun executeInCriticalSection(
        distributedLock: DistributedLock,
        lockKey: String,
        runnable: () -> Any?,
    ): Any?

}
