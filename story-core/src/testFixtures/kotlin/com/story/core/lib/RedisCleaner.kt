package com.story.core.lib

import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisCleaner(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>,
) {

    suspend fun cleanup(): Job {
        return coroutineScope {
            launch {
                val connection = reactiveRedisTemplate.connectionFactory.reactiveConnection
                connection.serverCommands().flushDb().awaitSingle()
            }
        }
    }

}
