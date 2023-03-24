package com.story.platform.core.helper

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties
import org.springframework.data.cassandra.core.cql.ReactiveCqlOperations
import org.springframework.stereotype.Component

@Component
class CassandraCleaner(
    private val cassandraProperties: CassandraProperties,
    private val reactiveCqlOperations: ReactiveCqlOperations,
) {

    suspend fun cleanUp() {
        val query =
            "SELECT table_name FROM system_schema.tables WHERE keyspace_name = '${cassandraProperties.keyspaceName}'"
        for (result in reactiveCqlOperations.queryForFlux(query).asFlow().toList()) {
            result.values.forEach { tableName ->
                reactiveCqlOperations.execute("TRUNCATE $tableName").awaitSingleOrNull()
            }
        }
    }

}
