package com.story.platform.core.infrastructure.cassandra

import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.cassandra.core.InsertOptions
import org.springframework.data.cassandra.core.ReactiveCassandraBatchOperations

fun ReactiveCassandraBatchOperations.upsert(entity: Any) = this.insert(entity, upsertOptions)

fun ReactiveCassandraBatchOperations.upsert(vararg entities: Any) = this.insert(entities, upsertOptions)

fun ReactiveCassandraBatchOperations.upsert(entities: Iterable<*>) = this.insert(entities, upsertOptions)

suspend fun ReactiveCassandraBatchOperations.executeCoroutine() = this.execute().awaitSingleOrNull()

private val upsertOptions = InsertOptions.builder()
    .withInsertNulls()
    .build()
