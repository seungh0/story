package com.story.core.support.redis

import java.time.Duration

interface StringRedisRepository<K : StringRedisKey<K, V>, V> {

    suspend fun exists(key: K): Boolean

    suspend fun existsBulk(keys: List<K>, batchSize: Int = 300): Map<K, Boolean>

    suspend fun get(key: K): V?

    suspend fun getBulk(keys: List<K>, batchSize: Int = 300): List<V?>

    suspend fun getBulkMap(keys: List<K>, batchSize: Int = 300): Map<K, V?> {
        if (keys.isEmpty()) {
            return emptyMap()
        }
        val values = getBulk(keys = keys, batchSize = batchSize)
        return keys.zip(values).toMap()
    }

    suspend fun scan(prefix: String, batchSize: Int = 300): List<String>

    suspend fun set(key: K, value: V) {
        setWithTtl(key = key, value = value, ttl = key.getTtl())
    }

    suspend fun setWithTtl(key: K, value: V, ttl: Duration?)

    suspend fun setIfAbsent(key: K, value: V): Boolean {
        return setIfAbsentWithTtl(key = key, value = value, ttl = key.getTtl())
    }

    suspend fun setIfAbsentWithTtl(key: K, value: V, ttl: Duration?): Boolean

    suspend fun setIfPresent(key: K, value: V): Boolean {
        return setIfPresentWithTtl(key = key, value = value, ttl = key.getTtl())
    }

    suspend fun setIfPresentWithTtl(key: K, value: V, ttl: Duration?): Boolean

    suspend fun setBulk(keys: Set<K>, value: V, batchSize: Int = 100, concurrency: Int = 1) {
        this.setBulk(keyValues = keys.associateWith { value }, batchSize = batchSize, concurrency = concurrency)
    }

    suspend fun setBulk(keyValues: Map<K, V>, batchSize: Int = 100, concurrency: Int = 1)

    suspend fun del(key: K): Boolean

    suspend fun delBulk(keys: Set<K>, batchSize: Int = 100, concurrency: Int = 1)

    suspend fun unlink(key: K): Boolean

    suspend fun unlinkBulk(keys: Set<K>, batchSize: Int = 300, concurrency: Int = 1)

    suspend fun incr(key: K): Long {
        return incrBy(key = key, count = 1)
    }

    suspend fun incrBy(key: K, count: Long): Long

    suspend fun incrBulk(keys: Set<K>, batchSize: Int = 10, concurrency: Int = 5) {
        incrBulkBy(keys = keys, count = 1, batchSize = batchSize, concurrency = concurrency)
    }

    suspend fun incrBulkBy(keys: Set<K>, count: Long, batchSize: Int = 10, concurrency: Int = 5)

    suspend fun decr(key: K): Long {
        return decrBy(key = key, count = 1L)
    }

    suspend fun decrBy(key: K, count: Long): Long

    suspend fun decrBulk(keys: Set<K>, batchSize: Int = 10, concurrency: Int = 5) {
        decrBulkBy(keys = keys, count = 1, batchSize = batchSize, concurrency = concurrency)
    }

    suspend fun decrBulkBy(keys: Set<K>, count: Long, batchSize: Int = 10, concurrency: Int = 5)

    suspend fun getTtl(key: K): Duration

    suspend fun setTtl(key: K, duration: Duration): Boolean

}
