package com.story.core.lib

import com.story.core.infrastructure.cassandra.CassandraBasicRepository
import com.story.core.infrastructure.cassandra.CassandraEntity
import com.story.core.infrastructure.cassandra.CassandraKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

class StubCassandraBasicRepository<T : CassandraEntity, K : CassandraKey> : CassandraBasicRepository<T, K> {

    val database = mutableMapOf<K, T>()

    fun clear() {
        database.clear()
    }

    override fun findAll(): Flow<T> {
        return database.values.asFlow()
    }

    override suspend fun deleteAll(entities: Iterable<T>) {
        entities.forEach { entity -> database.remove(key = entity.key) }
    }

    override suspend fun deleteAllById(ids: Iterable<K>) {
        ids.forEach { key -> database.remove(key = key) }
    }

    override suspend fun delete(entity: T) {
        database.remove(key = entity.key)
    }

    override fun <S : T> saveAll(entities: Iterable<S>): Flow<S> {
        entities.map { entity -> database[entity.key as K] = entity }
        return entities.asFlow()
    }

    override suspend fun <S : T> save(entity: S): T {
        database[entity.key as K] = entity
        return entity
    }

    override suspend fun existsById(id: K): Boolean {
        return database.containsKey(id)
    }

    override suspend fun findById(id: K): T? {
        return database[id]
    }

}
