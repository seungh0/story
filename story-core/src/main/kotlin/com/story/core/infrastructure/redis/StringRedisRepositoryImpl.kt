package com.story.core.infrastructure.redis

import com.story.core.common.coroutine.CoroutineParallelExecutor
import com.story.core.common.utils.mapToSet
import kotlinx.coroutines.coroutineScope
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
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>,
) : StringRedisRepository<K, V> {

    override suspend fun exists(key: K): Boolean {
        return reactiveRedisTemplate.hasKey(key.makeKeyString()).awaitSingle()
    }

    override suspend fun existsBulk(keys: List<K>, batchSize: Int): Map<K, Boolean> = coroutineScope {
        if (keys.isEmpty()) {
            return@coroutineScope emptyMap()
        }
        val operation = reactiveRedisTemplate.opsForValue()
        val values: List<String?> = keys.chunked(batchSize)
            .map { chunkedKeys -> operation.multiGet(chunkedKeys.map { key -> key.makeKeyString() }).awaitSingle() }
            .flatten()

        return@coroutineScope keys.zip(values)
            .associate { (key, value) -> key to !value.isNullOrBlank() } // ReactiveRedisRepository mget에서 없는 값이 null이 아닌 ""을 반환하는 이슈가 있어서 대응 [https://github.com/spring-projects/spring-data-redis/issues/2402]
    }

    override suspend fun get(key: K): V? {
        return key.deserializeValue(reactiveRedisTemplate.opsForValue()[key.makeKeyString()].awaitSingleOrNull())
    }

    override suspend fun getBulk(keys: List<K>, batchSize: Int): List<V?> = coroutineScope {
        if (keys.isEmpty()) {
            return@coroutineScope emptyList()
        }
        val values: List<String?> = keys.chunked(batchSize)
            .map { chunkedKeys: List<K> ->
                reactiveRedisTemplate.opsForValue().multiGet(chunkedKeys.map { key -> key.makeKeyString() })
                    .awaitSingle()
            }.flatten()

        return@coroutineScope values.map { value: String? -> getValue(keyType = keys[0], value = value) }
    }

    // ReactiveRedisRepository mget에서 없는 값이 null이 아닌 ""을 반환하는 이슈가 있어서 의도적으로 null으로 만들어준다
    // https://github.com/spring-projects/spring-data-redis/issues/2402
    private fun getValue(keyType: K, value: String?): V? {
        if (value.isNullOrBlank()) {
            return null
        }
        return keyType.deserializeValue(value)
    }

    override suspend fun scan(prefix: String, batchSize: Int): List<String> {
        val keyByteArrays = reactiveRedisTemplate.execute { action ->
            action.keyCommands().scan(
                ScanOptions.scanOptions()
                    .match("$prefix*")
                    .count(batchSize.toLong())
                    .build(),
            ).collectList()
        }.awaitSingle()

        return keyByteArrays.map { keyByteArray -> StandardCharsets.UTF_8.decode(keyByteArray).toString() }
    }

    override suspend fun setWithTtl(key: K, value: V, ttl: Duration?) {
        if (ttl == null) {
            reactiveRedisTemplate.opsForValue().set(key.makeKeyString(), key.serializeValue(value)).awaitSingle()
            return
        }
        reactiveRedisTemplate.opsForValue().set(key.makeKeyString(), key.serializeValue(value), ttl).awaitSingle()
    }

    override suspend fun setIfAbsentWithTtl(key: K, value: V, ttl: Duration?): Boolean {
        if (ttl == null) {
            return reactiveRedisTemplate.opsForValue().setIfAbsent(key.makeKeyString(), key.serializeValue(value))
                .awaitSingle()
        }
        return reactiveRedisTemplate.opsForValue().setIfAbsent(key.makeKeyString(), key.serializeValue(value), ttl)
            .awaitSingle()
    }

    override suspend fun setIfPresentWithTtl(key: K, value: V, ttl: Duration?): Boolean {
        if (ttl == null) {
            return reactiveRedisTemplate.opsForValue().setIfPresent(key.makeKeyString(), key.serializeValue(value))
                .awaitSingle()
        }
        return reactiveRedisTemplate.opsForValue().setIfPresent(key.makeKeyString(), key.serializeValue(value), ttl)
            .awaitSingle()
    }

    override suspend fun setBulk(keyValues: Map<K, V>, batchSize: Int, concurrency: Int) = coroutineScope {
        if (keyValues.isEmpty()) {
            return@coroutineScope
        }
        CoroutineParallelExecutor.executeJob(
            tasks = keyValues.entries,
            batchSize = batchSize,
            concurrency = concurrency,
        ) { chunkedEntries ->
            reactiveRedisTemplate.opsForValue()
                .multiSet(chunkedEntries.associate { (key, value) -> key.makeKeyString() to key.serializeValue(value) })
                .awaitSingle()
            chunkedEntries.asSequence()
                .map { entry -> entry.key }
                .filter { key: K -> key.getTtl() != null }
                .forEach { key: K -> reactiveRedisTemplate.expire(key.makeKeyString(), key.getTtl()!!).awaitSingle() }
        }
    }

    override suspend fun del(key: K): Boolean {
        return reactiveRedisTemplate.delete(key.makeKeyString()).awaitSingle() == 1L
    }

    override suspend fun delBulk(keys: Set<K>, batchSize: Int, concurrency: Int) = coroutineScope {
        if (keys.isEmpty()) {
            return@coroutineScope
        }

        CoroutineParallelExecutor.executeJob(
            tasks = keys,
            batchSize = batchSize,
            concurrency = concurrency,
        ) { chunkedKeys ->
            val chunkedKeyStrings = chunkedKeys.mapToSet { key -> key.makeKeyString() }
            reactiveRedisTemplate.delete(*chunkedKeyStrings.toTypedArray()).awaitSingle()
        }
    }

    override suspend fun unlink(key: K): Boolean {
        return reactiveRedisTemplate.unlink(key.makeKeyString()).awaitSingle() == 1L
    }

    override suspend fun unlinkBulk(keys: Set<K>, batchSize: Int, concurrency: Int) = coroutineScope {
        if (keys.isEmpty()) {
            return@coroutineScope
        }
        CoroutineParallelExecutor.executeJob(
            tasks = keys,
            batchSize = batchSize,
            concurrency = concurrency,
        ) { chunkedKeys ->
            reactiveRedisTemplate.unlink(*chunkedKeys.map { key -> key.makeKeyString() }.toTypedArray()).awaitSingle()
        }
    }

    override suspend fun incrBy(key: K, count: Long): Long {
        return reactiveRedisTemplate.opsForValue().increment(key.makeKeyString(), count).awaitSingle()
    }

    override suspend fun incrBulkBy(keys: Set<K>, count: Long, batchSize: Int, concurrency: Int) = coroutineScope {
        CoroutineParallelExecutor.executeJob(
            tasks = keys,
            batchSize = batchSize,
            concurrency = concurrency,
        ) { chunkedKeys ->
            chunkedKeys.map { key ->
                reactiveRedisTemplate.opsForValue().increment(key.makeKeyString(), count).awaitSingle()
            }
        }
    }

    override suspend fun decrBy(key: K, count: Long): Long {
        return reactiveRedisTemplate.opsForValue().decrement(key.makeKeyString(), count).awaitSingle()
    }

    override suspend fun decrBulkBy(keys: Set<K>, count: Long, batchSize: Int, concurrency: Int) = coroutineScope {
        CoroutineParallelExecutor.executeJob(
            tasks = keys,
            batchSize = batchSize,
            concurrency = concurrency,
        ) { chunkedKeys ->
            chunkedKeys.map { key ->
                reactiveRedisTemplate.opsForValue().decrement(key.makeKeyString(), count).awaitSingle()
            }
        }
    }

    override suspend fun getTtl(key: K): Duration {
        return reactiveRedisTemplate.getExpire(key.makeKeyString()).awaitSingle()
    }

    override suspend fun setTtl(key: K, duration: Duration): Boolean {
        return reactiveRedisTemplate.expire(key.makeKeyString(), duration).awaitSingleOrNull() ?: false
    }

}
