package com.story.platform.core.helper

import org.springframework.stereotype.Component

@Component
class TestCleaner(
    private val cassandraCleaner: CassandraCleaner,
    private val cacheCleaner: CacheCleaner,
) {

    suspend fun cleanUp() {
        cacheCleaner.cleanUp()
        cassandraCleaner.cleanUp()
    }

}
