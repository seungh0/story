package com.story.platform.core.helper

import org.springframework.boot.autoconfigure.cassandra.CassandraProperties
import org.springframework.data.cassandra.core.ReactiveCassandraTemplate
import org.springframework.stereotype.Component

@Component
class CassandraCleaner(
    private val reactiveCassandraTemplate: ReactiveCassandraTemplate,
    private val cassandraProperties: CassandraProperties,
) {

    fun cleanUp() {
        val tables = mutableSetOf<String>()
        reactiveCassandraTemplate.reactiveCqlOperations.queryForFlux(
            "SELECT table_name FROM system_schema.tables WHERE keyspace_name = ?",
            String::class.java,
            cassandraProperties.keyspaceName,
        ).collectList() .subscribe { table -> tables += table }

        println(tables)
        tables.forEach { table ->
            reactiveCassandraTemplate.reactiveCqlOperations.queryForResultSet(
                "DROP table $table;"
            ).subscribe()
        }
    }

}
