package com.story.platform.core.lib

import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ScanOptions
import org.springframework.stereotype.Component

@Component
class RedisCleaner(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>,
) {

    suspend fun cleanup(): List<Job> {
        val keyStrings = reactiveRedisTemplate.execute { action ->
            action.keyCommands().scan(
                ScanOptions.scanOptions()
                    .match("*")
                    .count(300)
                    .build()
            ).collectList()
        }.awaitSingle()

        return coroutineScope {
            return@coroutineScope keyStrings.map {
                launch {
                    reactiveRedisTemplate.execute { action ->
                        action.keyCommands().del(it)
                    }.awaitSingle()
                }
            }
        }
    }

}
