package com.story.platform.core.helper

import com.story.platform.core.support.coroutine.IOBound
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties
import org.springframework.data.cassandra.core.cql.ReactiveCqlOperations
import org.springframework.stereotype.Component

@Component
class CassandraCleaner(
    private val cassandraProperties: CassandraProperties,
    private val reactiveCqlOperations: ReactiveCqlOperations,

    @IOBound
    private val dispatcher: CoroutineDispatcher,
) {

    suspend fun cleanUp(): List<Job> {
        val query = "SELECT table_name FROM system_schema.tables WHERE keyspace_name = '${cassandraProperties.keyspaceName}'"
        return withContext(dispatcher) {
            val jobs = mutableListOf<Job>()
            for (result in reactiveCqlOperations.queryForFlux(query).asFlow().toList()) {
                result.values.map { tableName ->
                    jobs += launch {
                        reactiveCqlOperations.execute("TRUNCATE $tableName").awaitSingleOrNull()
                    }
                }
            }
            return@withContext jobs
        }
    }

}
