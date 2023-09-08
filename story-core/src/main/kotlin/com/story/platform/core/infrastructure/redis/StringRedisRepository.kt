package com.story.platform.core.infrastructure.redis

import java.time.Duration

interface StringRedisRepository<K : StringRedisKey<K, V>, V> {

    suspend fun exists(key: K): Boolean

    suspend fun existsBulk(keys: List<K>): Map<K, Boolean?>

    suspend fun get(key: K): V?

    suspend fun getBulk(keys: List<K>): List<V?>

    suspend fun getBulkMap(keys: List<K>): Map<K, V?> {
        if (keys.isEmpty()) {
            return HashMap()
        }
        return keys.zip(getBulk(keys)).toMap()
    }

    suspend fun set(key: K, value: V) {
        setWithTtl(key, value, key.getTtl())
    }

    suspend fun setBulk(keys: Set<K>, value: V) {
        this.setBulk(keys.associateWith { value })
    }

    suspend fun setIfAbsent(key: K, value: V): Boolean {
        return this.setIfAbsentWithTtl(key = key, value = value, ttl = key.getTtl())
    }

    suspend fun setIfPresent(key: K, value: V): Boolean {
        return this.setIfPresentWithTtl(key = key, value = value, ttl = key.getTtl())
    }

    suspend fun setWithTtl(key: K, value: V, ttl: Duration?)

    suspend fun setBulk(keyValues: Map<K, V>)

    suspend fun setIfAbsentWithTtl(key: K, value: V, ttl: Duration?): Boolean

    suspend fun setIfPresentWithTtl(key: K, value: V, ttl: Duration?): Boolean

    suspend fun del(key: K)

    suspend fun delBulk(keys: Set<K>)

    suspend fun incr(key: K): Long {
        return incrBy(key = key, count = 1)
    }

    suspend fun incrBy(key: K, count: Long): Long

    suspend fun incrBulk(keys: Set<K>): Map<K, Long> {
        return incrBulkBy(keys = keys, count = 1)
    }

    suspend fun incrBulkBy(keys: Set<K>, count: Long): Map<K, Long>

    suspend fun decr(key: K): Long {
        return decrBy(key = key, count = 1L)
    }

    suspend fun decrBy(key: K, count: Long): Long

    suspend fun decrBulk(keys: Set<K>): Map<K, Long> {
        return decrBulkBy(keys = keys, count = 1)
    }

    suspend fun decrBulkBy(keys: Set<K>, count: Long): Map<K, Long>

    suspend fun getTtl(key: K): Duration

    suspend fun setTtl(key: K, duration: Duration)

    suspend fun scan(prefix: String): List<String>

}
