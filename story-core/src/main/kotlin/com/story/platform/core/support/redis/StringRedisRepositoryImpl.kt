package com.story.platform.core.support.redis

import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class StringRedisRepositoryImpl<K : StringRedisKey<K, V>, V>(
    private val redisTemplate: ReactiveRedisTemplate<String, String>,
) : StringRedisRepository<K, V> {

    override suspend fun exists(key: K): Boolean {
        return redisTemplate.opsForValue().get(key.getKey()).awaitSingleOrNull() != null
    }

    override suspend fun existsBulk(keys: List<K>): Map<K, Boolean> {
        if (keys.isEmpty()) {
            return HashMap()
        }
        val values: List<String?> = keys.chunked(FETCH_SIZE).mapNotNull { chunkedKeys ->
            redisTemplate.opsForValue().multiGet(chunkedKeys.map { key -> key.getKey() }).awaitSingleOrNull()
        }
            .flatten()

        val exists: MutableMap<K, Boolean> = HashMap()
        for (i in keys.indices) {
            exists[keys[i]] = values[i] != null
        }
        return exists
    }

    override suspend fun get(key: K): V? {
        return key.deserializeValue(redisTemplate.opsForValue().get(key.getKey()).awaitSingleOrNull())
    }

    override suspend fun getBulk(keys: List<K>): List<V> {
        if (keys.isEmpty()) {
            return emptyList()
        }
        val actualType = keys[0]
        val values: List<String> = keys.chunked(FETCH_SIZE)
            .mapNotNull { keyList ->
                redisTemplate.opsForValue().multiGet(keyList.map { key -> key.getKey() }).awaitSingleOrNull()
            }
            .flatten()

        return values.mapNotNull { value -> actualType.deserializeValue(value) }
    }

    override suspend fun setWithTtl(key: K, value: V, ttl: Duration?) {
        if (ttl == null) {
            redisTemplate.opsForValue().set(key.getKey(), key.serializeValue(value)).awaitSingleOrNull()
            return
        }
        redisTemplate.opsForValue().set(key.getKey(), key.serializeValue(value), ttl).awaitSingleOrNull()
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
        redisTemplate.delete(key.getKey()).awaitSingleOrNull()
    }

    override suspend fun delBulk(keys: List<K>) {
        if (keys.isEmpty()) {
            return
        }

        keys.asSequence()
            .chunked(FETCH_SIZE)
            .forEach { chunkedKeys ->
                val targetKeySet = chunkedKeys.asSequence()
                    .map { key -> key.getKey() }
                    .toSet()
                redisTemplate.delete(*targetKeySet.toTypedArray()).awaitSingleOrNull()
            }
    }

    override suspend fun incrBy(key: K, value: Long): Long {
        return redisTemplate.opsForValue().increment(key.getKey(), value).awaitSingleOrNull() ?: 0L
    }

    override suspend fun decrBy(key: K, value: Long): Long {
        return redisTemplate.opsForValue().decrement(key.getKey(), value).awaitSingleOrNull() ?: 0L
    }

    override suspend fun getTtl(key: K): Duration {
        return redisTemplate.getExpire(key.getKey()).awaitSingleOrNull() ?: Duration.ZERO
    }

    companion object {
        private const val FETCH_SIZE = 1_000
    }

}
