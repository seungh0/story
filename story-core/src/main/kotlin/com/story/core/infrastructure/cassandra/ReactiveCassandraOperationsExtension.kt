package com.story.core.infrastructure.cassandra

import org.springframework.data.cassandra.core.DeleteOptions
import org.springframework.data.cassandra.core.EntityWriteResult
import org.springframework.data.cassandra.core.InsertOptions
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.data.cassandra.core.WriteResult
import reactor.core.publisher.Mono

fun <T : Any> ReactiveCassandraOperations.insertIfNotExists(entity: T): Mono<EntityWriteResult<T>> {
    return this.insert(entity, InsertOptions.builder().withIfNotExists().build())
}

fun <T : Any> ReactiveCassandraOperations.deleteIfExists(entity: T): Mono<WriteResult> {
    return this.delete(entity, DeleteOptions.builder().withIfExists().build())
}
