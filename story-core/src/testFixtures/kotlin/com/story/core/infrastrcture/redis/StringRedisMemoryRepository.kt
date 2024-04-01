package com.story.core.infrastrcture.redis

import com.story.core.infrastructure.redis.StringRedisKey
import com.story.core.infrastructure.redis.StringRedisRepository
import java.time.Duration

class StringRedisMemoryRepository<K : StringRedisKey<K, Long>> : StringRedisRepository<K, Long> {

    private val map = mutableMapOf<K, Long>()

    fun clear() {
        map.clear()
    }

    override suspend fun exists(key: K): Boolean {
        return map.containsKey(key)
    }

    override suspend fun existsBulk(keys: List<K>, batchSize: Int): Map<K, Boolean> {
        return keys.associateWith { key ->
            map.containsKey(key)
        }
    }

    override suspend fun get(key: K): Long? {
        return map[key]
    }

    override suspend fun getBulk(keys: List<K>, batchSize: Int): List<Long?> {
        return keys.map { key -> map[key] }
    }

    override suspend fun scan(prefix: String, batchSize: Int): List<String> {
        TODO("Not yet implemented")
    }

    override suspend fun setWithTtl(key: K, value: Long, ttl: Duration?) {
        map[key] = value
    }

    override suspend fun setIfAbsentWithTtl(key: K, value: Long, ttl: Duration?): Boolean {
        if (!map.containsKey(key)) {
            map[key] = value
            return true
        }
        return false
    }

    override suspend fun setIfPresentWithTtl(key: K, value: Long, ttl: Duration?): Boolean {
        if (map.containsKey(key)) {
            map[key] = value
            return true
        }
        return false
    }

    override suspend fun setBulk(keyValues: Map<K, Long>, batchSize: Int, concurrency: Int) {
        keyValues.forEach { (key, value) ->
            map[key] = value
        }
    }

    override suspend fun del(key: K): Boolean {
        return map.remove(key) != null
    }

    override suspend fun delBulk(keys: Set<K>, batchSize: Int, concurrency: Int) {
        keys.forEach { key ->
            map.remove(key)
        }
    }

    override suspend fun unlink(key: K): Boolean {
        return map.remove(key) != null
    }

    override suspend fun unlinkBulk(keys: Set<K>, batchSize: Int, concurrency: Int) {
        keys.map { key ->
            map.remove(key)
        }
    }

    override suspend fun incrBy(key: K, count: Long): Long {
        val current = map[key] ?: 0L
        val newCount = current + count
        map[key] = newCount
        return newCount
    }

    override suspend fun incrBulkBy(keys: Set<K>, count: Long, batchSize: Int, concurrency: Int) {
        keys.forEach { key ->
            val current = map[key] ?: 0L
            val newCount = current + count
            map[key] = newCount
        }
    }

    override suspend fun decrBy(key: K, count: Long): Long {
        val current = map[key] ?: 0L
        val newCount = current - count
        map[key] = newCount
        return newCount
    }

    override suspend fun decrBulkBy(keys: Set<K>, count: Long, batchSize: Int, concurrency: Int) {
        keys.forEach { key ->
            val current = map[key] ?: 0L
            val newCount = current - count
            map[key] = newCount
        }
    }

    override suspend fun getTtl(key: K): Duration {
        TODO("Not yet implemented")
    }

    override suspend fun setTtl(key: K, duration: Duration): Boolean {
        TODO("Not yet implemented")
    }

}
