package com.story.core.lib

import kotlinx.coroutines.joinAll
import org.springframework.stereotype.Component

@Component
class TestCleaner(
    private val cassandraCleaner: CassandraCleaner,
    private val cacheCleaner: CacheCleaner,
    private val redisCleaner: RedisCleaner,
) {

    suspend fun cleanUp() {
        val cacheCleanJob = cacheCleaner.cleanUp()
        val cassandraCleanJob = cassandraCleaner.cleanUp()
        val redisCleanJob = redisCleaner.cleanup()
        cacheCleanJob.joinAll()
        cassandraCleanJob.joinAll()
        redisCleanJob.joinAll()
    }

}
