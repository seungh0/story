package com.story.core.infrastructure.cassandra

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.cassandra.core.InsertOptions
import org.springframework.data.cassandra.core.ReactiveCassandraBatchOperations
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.data.cassandra.core.UpdateOptions
import java.time.Duration

fun ReactiveCassandraBatchOperations.upsert(entity: Any) = this.insert(entity, upsertOptions)
fun ReactiveCassandraBatchOperations.upsert(vararg entities: Any) = this.insert(entities, upsertOptions)
fun ReactiveCassandraBatchOperations.upsert(entities: Iterable<*>) = this.insert(entities, upsertOptions)
fun ReactiveCassandraOperations.expire(entities: Any, ttl: Duration) = this.update(
    entities,
    UpdateOptions.builder()
        .ttl(ttl)
        .build()
)

suspend fun ReactiveCassandraBatchOperations.executeCoroutine() = this.execute().awaitSingle()

private val upsertOptions = InsertOptions.builder()
    .withInsertNulls()
    .build()
