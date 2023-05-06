package com.story.platform.core.infrastructure.redis

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

    override suspend fun getBulk(keys: List<K>): List<V> {
        if (keys.isEmpty()) {
            return emptyList()
        }
        val actualType = keys[0]
        val values: List<String> = keys.chunked(FETCH_SIZE)
            .mapNotNull { keyList ->
                redisTemplate.opsForValue().multiGet(keyList.map { key -> key.makeKeyString() }).awaitSingleOrNull()
            }
            .flatten()

        return values.mapNotNull { value -> actualType.deserializeValue(value) }
    }

    override suspend fun setWithTtl(key: K, value: V, ttl: Duration?) {
        if (ttl == null) {
            redisTemplate.opsForValue().set(key.makeKeyString(), key.serializeValue(value)).awaitSingleOrNull()
            return
        }
        redisTemplate.opsForValue().set(key.makeKeyString(), key.serializeValue(value), ttl).awaitSingleOrNull()
    }

    override suspend fun setBulk(keyValues: Map<K, V>) {
        if (keyValues.isEmpty()) {
            return
        }

        redisTemplate.opsForValue()
            .multiSet(keyValues.map { (key, value) -> key.toString() to value.toString() }.toMap())
            .awaitSingleOrNull()
    }

    override suspend fun del(key: K) {
        redisTemplate.delete(key.makeKeyString()).awaitSingleOrNull()
    }

    override suspend fun delBulk(keys: List<K>) {
        if (keys.isEmpty()) {
            return
        }

        keys.asSequence()
            .chunked(FETCH_SIZE)
            .forEach { chunkedKeys ->
                val targetKeySet = chunkedKeys.asSequence()
                    .map { key -> key.makeKeyString() }
                    .toSet()
                redisTemplate.delete(*targetKeySet.toTypedArray()).awaitSingleOrNull()
            }
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
