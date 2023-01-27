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
        val operations = redisTemplate.opsForValue()
        return operations[key.getKey()].awaitSingleOrNull() != null
    }

    override suspend fun existsBulk(keys: List<K>): Map<K, Boolean> {
        if (keys.isEmpty()) {
            return HashMap()
        }
        val operations = redisTemplate.opsForValue()

        val values: List<String?> = keys.chunked(FETCH_SIZE)
            .mapNotNull { chunkedKeys ->
                operations.multiGet(chunkedKeys.map { key -> key.getKey() }).awaitSingleOrNull()
            }
            .flatten()

        val exists: MutableMap<K, Boolean> = HashMap()
        for (i in keys.indices) {
            exists[keys[i]] = values[i] != null
        }
        return exists
    }

    override suspend fun get(key: K): V? {
        val operations = redisTemplate.opsForValue()
        return key.deserializeValue(operations[key.getKey()].awaitSingleOrNull())
    }

    override suspend fun getBulk(keys: List<K>): List<V> {
        if (keys.isEmpty()) {
            return emptyList()
        }
        val actualType = keys[0]
        val operations = redisTemplate.opsForValue()
        val values: List<String> = keys.chunked(FETCH_SIZE)
            .mapNotNull { keyList -> operations.multiGet(keyList.map { key -> key.getKey() }).awaitSingleOrNull() }
            .flatten()

        return values.mapNotNull { value -> actualType.deserializeValue(value) }
    }

    override suspend fun setWithTtl(key: K, value: V, ttl: Duration?) {
        val operations = redisTemplate.opsForValue()
        if (ttl == null) {
            operations[key.getKey()] = key.serializeValue(value)
            return
        }
        operations[key.getKey(), key.serializeValue(value)] = ttl
    }

    override suspend fun setBulk(keyValues: Map<K, V>) {
        if (keyValues.isEmpty()) {
            return
        }

        val operations = redisTemplate.opsForValue()
        operations.multiSet(keyValues.map { (key, value) -> key.toString() to value.toString() }.toMap())
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
        val operations = redisTemplate.opsForValue()
        return operations.increment(key.getKey(), value).awaitSingleOrNull() ?: 0L
    }

    override suspend fun decrBy(key: K, value: Long): Long {
        val operations = redisTemplate.opsForValue()
        return operations.decrement(key.getKey(), value).awaitSingleOrNull() ?: 0L
    }

    companion object {
        private const val FETCH_SIZE = 1_000
    }

}
