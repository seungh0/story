package com.story.core.lib

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

    suspend fun cleanup(): Job {
        val keyBytes = reactiveRedisTemplate.execute { action ->
            action.keyCommands().scan(
                ScanOptions.scanOptions()
                    .match("*")
                    .count(300)
                    .build()
            ).collectList()
        }.awaitSingle()

        return coroutineScope {
            launch {
                if (keyBytes.isEmpty()) {
                    return@launch
                }
                reactiveRedisTemplate.execute { pipeline ->
                    pipeline.keyCommands().mUnlink(keyBytes)
                }.awaitSingle()
            }
        }
    }

}
