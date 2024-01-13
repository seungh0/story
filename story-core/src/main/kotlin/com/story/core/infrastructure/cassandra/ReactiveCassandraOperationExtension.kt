package com.story.core.infrastructure.cassandra

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.cassandra.core.InsertOptions
import org.springframework.data.cassandra.core.ReactiveCassandraBatchOperations
import java.time.Duration

fun ReactiveCassandraBatchOperations.upsert(entity: Any, ttl: Duration? = null) =
    this.insert(entity, upsertOptions(ttl))

fun ReactiveCassandraBatchOperations.upsert(vararg entities: Any, ttl: Duration? = null) =
    this.insert(entities, upsertOptions(ttl))

fun ReactiveCassandraBatchOperations.upsert(entities: Iterable<*>, ttl: Duration? = null) =
    this.insert(entities, upsertOptions(ttl))

suspend fun ReactiveCassandraBatchOperations.executeCoroutine() = this.execute().awaitSingle()

private fun upsertOptions(ttl: Duration?) = InsertOptions.builder()
    .withInsertNulls()
    .also { option ->
        ttl?.let { option.ttl(ttl) }
    }
    .build()
