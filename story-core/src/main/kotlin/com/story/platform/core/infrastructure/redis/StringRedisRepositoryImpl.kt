package com.story.platform.core.infrastructure.redis

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ScanOptions
import org.springframework.stereotype.Repository
import java.nio.charset.StandardCharsets
import java.time.Duration

@Repository
class StringRedisRepositoryImpl<K : StringRedisKey<K, V>, V>(
    private val redisTemplate: ReactiveRedisTemplate<String, String>,
) : StringRedisRepository<K, V> {

    override suspend fun exists(key: K): Boolean {
        return redisTemplate.opsForValue()[key.makeKeyString()].awaitSingleOrNull() != null
    }

    override suspend fun existsBulk(keys: List<K>): Map<K, Boolean> {
        if (keys.isEmpty()) {
            return emptyMap()
        }
        val values: List<String?> = keys.chunked(CHUNK_SIZE).mapNotNull { chunkedKeys ->
            redisTemplate.opsForValue().multiGet(chunkedKeys.map { key -> key.makeKeyString() }).awaitSingleOrNull()
        }.flatten()

        return keys.zip(values)
            .associate { (key, value) -> key to !value.isNullOrBlank() }
    }

    override suspend fun get(key: K): V? {
        return key.deserializeValue(redisTemplate.opsForValue()[key.makeKeyString()].awaitSingleOrNull())
    }

    override suspend fun getBulk(keys: List<K>): List<V?> = coroutineScope {
        if (keys.isEmpty()) {
            return@coroutineScope emptyList()
        }
        val actualType = keys[0]
        val values: List<String?> = keys.chunked(CHUNK_SIZE).map { keyList ->
            async {
                redisTemplate.opsForValue().multiGet(keyList.map { key -> key.makeKeyString() }).awaitSingle()
            }
        }.awaitAll().flatten()

        return@coroutineScope values.map { value -> actualType.deserializeValue(value) }
    }

    override suspend fun setWithTtl(key: K, value: V, ttl: Duration?) {
        if (ttl == null) {
            redisTemplate.opsForValue().set(key.makeKeyString(), key.serializeValue(value)).awaitSingleOrNull()
            return
        }
        redisTemplate.opsForValue().set(key.makeKeyString(), key.serializeValue(value), ttl).awaitSingleOrNull()
    }

    override suspend fun setBulk(keyValues: Map<K, V>): Unit = coroutineScope {
        if (keyValues.isEmpty()) {
            return@coroutineScope
        }

        keyValues.entries.chunked(CHUNK_SIZE)
            .chunked(MAX_PARALLEL_COUNT)
            .map { distributedChunkedEntries ->
                distributedChunkedEntries.map { chunkedEntries ->
                    launch {
                        redisTemplate.opsForValue()
                            .multiSet(
                                chunkedEntries.associate { (key, value) ->
                                    key.makeKeyString() to key.serializeValue(
                                        value
                                    )
                                }
                            )
                            .awaitSingleOrNull()

                        chunkedEntries.asSequence()
                            .map { entry -> entry.key }
                            .filter { key: K -> key.getTtl() != null }
                            .forEach { key: K ->
                                redisTemplate.expire(key.makeKeyString(), key.getTtl()!!).awaitSingleOrNull()
                            }
                    }
                }.joinAll()
            }
    }

    override suspend fun setIfAbsentWithTtl(key: K, value: V, ttl: Duration?): Boolean {
        if (ttl == null) {
            return redisTemplate.opsForValue().setIfAbsent(key.makeKeyString(), key.serializeValue(value)).awaitSingle()
        }
        return redisTemplate.opsForValue().setIfAbsent(key.makeKeyString(), key.serializeValue(value), ttl)
            .awaitSingle()
    }

    override suspend fun setIfPresentWithTtl(key: K, value: V, ttl: Duration?): Boolean {
        if (ttl == null) {
            return redisTemplate.opsForValue().setIfPresent(key.makeKeyString(), key.serializeValue(value))
                .awaitSingle()
        }
        return redisTemplate.opsForValue().setIfPresent(key.makeKeyString(), key.serializeValue(value), ttl)
            .awaitSingle()
    }

    override suspend fun del(key: K) {
        redisTemplate.delete(key.makeKeyString()).awaitSingleOrNull()
    }

    override suspend fun delBulk(keys: Set<K>): Unit = coroutineScope {
        if (keys.isEmpty()) {
            return@coroutineScope
        }

        keys.chunked(CHUNK_SIZE)
            .chunked(MAX_PARALLEL_COUNT)
            .map { distributedChunkedKeys ->
                distributedChunkedKeys.map { chunkedKeys ->
                    launch {
                        val chunkedKeyStrings = chunkedKeys.asSequence()
                            .map { key -> key.makeKeyString() }
                            .toSet()
                        redisTemplate.delete(*chunkedKeyStrings.toTypedArray()).awaitSingleOrNull()
                    }
                }.joinAll()
            }
    }

    override suspend fun incrBy(key: K, count: Long): Long {
        return redisTemplate.opsForValue().increment(key.makeKeyString(), count).awaitSingleOrNull() ?: 0L
    }

    override suspend fun incrBulkBy(keys: Set<K>, count: Long): Map<K, Long> = coroutineScope {
        return@coroutineScope keys.asSequence().chunked(CHUNK_SIZE)
            .chunked(MAX_PARALLEL_COUNT)
            .map { distributedChunkedKeys ->
                distributedChunkedKeys.map { chunkedKeys ->
                    chunkedKeys.map { key ->
                        async {
                            key to redisTemplate.opsForValue().increment(key.makeKeyString(), count)
                                .awaitSingle()
                        }
                    }
                }
            }.flatten().flatten().toList().awaitAll().toMap()
    }

    override suspend fun decrBy(key: K, count: Long): Long {
        return redisTemplate.opsForValue().decrement(key.makeKeyString(), count).awaitSingleOrNull() ?: 0L
    }

    override suspend fun decrBulkBy(keys: Set<K>, count: Long): Map<K, Long> = coroutineScope {
        return@coroutineScope keys.asSequence().chunked(CHUNK_SIZE)
            .chunked(MAX_PARALLEL_COUNT)
            .map { distributedChunkedKeys ->
                distributedChunkedKeys.map { chunkedKeys ->
                    chunkedKeys.map { key ->
                        async {
                            key to redisTemplate.opsForValue().decrement(key.makeKeyString(), count).awaitSingle()
                        }
                    }
                }
            }.flatten().flatten().toList().awaitAll().toMap()
    }

    override suspend fun getTtl(key: K): Duration {
        return redisTemplate.getExpire(key.makeKeyString()).awaitSingleOrNull() ?: Duration.ZERO
    }

    override suspend fun setTtl(key: K, duration: Duration) {
        redisTemplate.expire(key.makeKeyString(), duration).awaitSingleOrNull()
    }

    override suspend fun scan(prefix: String): List<String> {
        val keyByteArrays = redisTemplate.execute { action ->
            action.keyCommands().scan(
                ScanOptions.scanOptions()
                    .match("$prefix*")
                    .count(CHUNK_SIZE.toLong())
                    .build()
            )
                .collectList()
        }.awaitSingle()

        return keyByteArrays.map { keyByteArray -> StandardCharsets.UTF_8.decode(keyByteArray).toString() }
    }

    companion object {
        private const val CHUNK_SIZE = 500
        private const val MAX_PARALLEL_COUNT = 3
    }

}
