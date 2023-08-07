package com.story.platform.core.infrastructure.redis

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactive.awaitSingle
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
            return HashMap()
        }
        val values: List<String?> = keys.chunked(FETCH_SIZE).mapNotNull { chunkedKeys ->
            redisTemplate.opsForValue().multiGet(chunkedKeys.map { key -> key.makeKeyString() }).awaitSingleOrNull()
        }
            .flatten()

        val exists: MutableMap<K, Boolean> = HashMap()
        for (i in keys.indices) {
            exists[keys[i]] = values[i] != null
        }
        return exists
    }

    override suspend fun get(key: K): V? {
        return key.deserializeValue(redisTemplate.opsForValue()[key.makeKeyString()].awaitSingleOrNull())
    }

    override suspend fun getBulk(keys: List<K>): List<V> = coroutineScope {
        if (keys.isEmpty()) {
            return@coroutineScope emptyList()
        }
        val actualType = keys[0]
        val values: List<String> = keys.chunked(FETCH_SIZE).map { keyList ->
            async {
                redisTemplate.opsForValue().multiGet(keyList.map { key -> key.makeKeyString() }).awaitSingleOrNull()
            }
        }.awaitAll().filterNotNull().flatten()

        return@coroutineScope values.mapNotNull { value -> actualType.deserializeValue(value) }
    }

    override suspend fun setWithTtl(key: K, value: V, ttl: Duration?) {
        if (ttl == null) {
            redisTemplate.opsForValue().set(key.makeKeyString(), key.serializeValue(value)).awaitSingleOrNull()
            return
        }
        redisTemplate.opsForValue().set(key.makeKeyString(), key.serializeValue(value), ttl).awaitSingleOrNull()
    }

    override suspend fun setBulk(keyValues: Map<K, V>) = coroutineScope {
        if (keyValues.isEmpty()) {
            return@coroutineScope
        }
        keyValues.entries.chunked(FETCH_SIZE).map { chunkedEntries ->
            async {
                redisTemplate.opsForValue()
                    .multiSet(chunkedEntries.associate { (key, value) -> key.makeKeyString() to key.serializeValue(value) })
                    .awaitSingleOrNull()

                chunkedEntries.asSequence()
                    .map { entry -> entry.key }
                    .filter { key: K -> key.getTtl() != null }
                    .forEach { key: K ->
                        redisTemplate.expire(key.makeKeyString(), key.getTtl()!!).awaitSingleOrNull()
                    }
            }
        }.awaitAll()
    }

    override suspend fun del(key: K) {
        redisTemplate.delete(key.makeKeyString()).awaitSingleOrNull()
    }

    override suspend fun delBulk(keys: List<K>) = coroutineScope {
        if (keys.isEmpty()) {
            return@coroutineScope
        }
        keys.chunked(FETCH_SIZE).map { chunkedKeys ->
            async {
                val chunkedKeyStrings = chunkedKeys.asSequence()
                    .map { key -> key.makeKeyString() }
                    .toSet()
                redisTemplate.delete(*chunkedKeyStrings.toTypedArray()).awaitSingleOrNull()
            }
        }.awaitAll()
    }

    override suspend fun incrBy(key: K, count: Long): Long {
        return redisTemplate.opsForValue().increment(key.makeKeyString(), count).awaitSingleOrNull() ?: 0L
    }

    override suspend fun decrBy(key: K, count: Long): Long {
        return redisTemplate.opsForValue().decrement(key.makeKeyString(), count).awaitSingleOrNull() ?: 0L
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
                    .count(FETCH_SIZE.toLong())
                    .build()
            )
                .collectList()
        }.awaitSingle()

        return keyByteArrays.map { keyByteArray -> StandardCharsets.UTF_8.decode(keyByteArray).toString() }
    }

    companion object {
        private const val FETCH_SIZE = 1_000
    }

}
