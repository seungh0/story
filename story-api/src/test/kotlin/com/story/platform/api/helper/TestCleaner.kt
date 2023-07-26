package com.story.platform.api.helper

import kotlinx.coroutines.joinAll
import org.springframework.stereotype.Component

@Component
class TestCleaner(
    private val cassandraCleaner: CassandraCleaner,
    private val cacheCleaner: CacheCleaner,
) {

    suspend fun cleanUp() {
        val cacheCleanJob = cacheCleaner.cleanUp()
        val cassandraCleanJob = cassandraCleaner.cleanUp()
        cacheCleanJob.joinAll()
        cassandraCleanJob.joinAll()
    }

}
