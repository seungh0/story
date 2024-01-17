package com.story.core.lib

import com.story.core.infrastructure.cassandra.CassandraCounterRepository
import com.story.core.infrastructure.cassandra.CassandraEntity
import com.story.core.infrastructure.cassandra.CassandraKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

class StubCassandraCounterRepository<T : CassandraEntity, K : CassandraKey> : CassandraCounterRepository<T, K> {

    val database = mutableMapOf<K, Pair<T, Long>>()

    fun clear() {
        database.clear()
    }

    fun getCount(key: K): Long {
        return database[key]?.second ?: 0L
    }

    override fun findAll(): Flow<T> {
        return database.values.map { it.first }.asFlow()
    }

    override suspend fun findById(id: K): T? {
        return database[id]?.first
    }

}
