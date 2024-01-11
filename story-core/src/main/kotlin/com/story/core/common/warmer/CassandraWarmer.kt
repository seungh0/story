package com.story.core.common.warmer

import com.datastax.oss.driver.internal.core.type.codec.TimeUuidCodec
import com.story.core.common.error.InternalServerException
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.cassandra.core.ReactiveCassandraTemplate
import org.springframework.data.cassandra.core.selectOne
import org.springframework.stereotype.Component

@Component
class CassandraWarmer(
    private val cassandraTemplate: ReactiveCassandraTemplate,
) : ExactlyOnceRunWarmer() {

    override suspend fun doRun() {
        try {
            cassandraTemplate.selectOne<TimeUuidCodec>("select now() from workspace_v1 limit 1").awaitSingle()
        } catch (e: Exception) {
            throw InternalServerException("Cassandra Warmer Failed", cause = e)
        }
    }

}
