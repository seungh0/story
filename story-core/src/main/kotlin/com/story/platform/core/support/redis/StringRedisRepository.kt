package com.story.platform.core.support.redis

import java.time.Duration
import java.util.stream.Collectors

interface StringRedisRepository<K : StringRedisKey<K, V>, V> {

    suspend fun exists(key: K): Boolean

    suspend fun existsBulk(keys: List<K>): Map<K, Boolean?>

    suspend fun get(key: K): V?

    suspend fun getBulk(keys: List<K>): List<V>

    suspend fun getBulkMap(keys: List<K>): Map<K, V> {
        if (keys.isEmpty()) {
            return HashMap()
        }
        val values = getBulk(keys)
        val keyValues: MutableMap<K, V> = HashMap()
        for (i in keys.indices) {
            keyValues[keys[i]] = values[i]
        }
        return keyValues
    }

    suspend fun set(key: K, value: V) {
        setWithTtl(key, value, key.getTtl())
    }

    suspend fun setBulk(keys: List<K>, value: V) {
        this.setBulk(
            keys.stream().collect(Collectors.toMap({ key -> key }, { value }))
        )
    }

    suspend fun setWithTtl(key: K, value: V, ttl: Duration?)

    suspend fun setBulk(keyValues: Map<K, V>)

    suspend fun del(key: K)

    suspend fun delBulk(keys: List<K>)

    suspend fun incr(key: K): Long {
        return incrBy(key, 1)
    }

    suspend fun incrBy(key: K, value: Long): Long

    suspend fun decr(key: K): Long {
        return decrBy(key, 1L)
    }

    suspend fun decrBy(key: K, value: Long): Long

}
