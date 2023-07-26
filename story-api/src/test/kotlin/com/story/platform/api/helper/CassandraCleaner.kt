package com.story.platform.api.helper

import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
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

    suspend fun cleanUp(): List<Job> {
        val query = "SELECT table_name FROM system_schema.tables WHERE keyspace_name = '${cassandraProperties.keyspaceName}'"
        return coroutineScope {
            val jobs = mutableListOf<Job>()
            for (result in reactiveCqlOperations.queryForFlux(query).asFlow().toList()) {
                result.values.map { tableName ->
                    jobs += launch {
                        reactiveCqlOperations.execute("TRUNCATE $tableName").awaitSingleOrNull()
                    }
                }
            }
            return@coroutineScope jobs
        }
    }

}
