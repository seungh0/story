package com.story.api.application.workspace

import com.story.core.common.annotation.EventConsumer
import com.story.core.common.annotation.IOBound
import com.story.core.common.json.toJson
import com.story.core.common.json.toObject
import com.story.core.common.logger.LoggerExtension.log
import com.story.core.domain.event.EventAction
import com.story.core.domain.event.EventRecord
import com.story.core.domain.workspace.WorkspaceEvent
import com.story.core.domain.workspace.WorkspaceLocalCacheEvictManager
import com.story.core.support.kafka.KafkaConsumerConfig
import com.story.core.support.kafka.RetryableKafkaListener
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.DltHandler
import org.springframework.messaging.handler.annotation.Headers
import org.springframework.messaging.handler.annotation.Payload

@EventConsumer
class WorkspaceCacheEvictConsumer(
    private val workspaceLocalCacheEvictManager: WorkspaceLocalCacheEvictManager,

    @IOBound
    private val dispatcher: CoroutineDispatcher,
) {

    @RetryableKafkaListener(
        topics = ["\${story.kafka.topic.workspace.name}"],
        groupId = "$GROUP_ID-\${random.uuid}",
        containerFactory = KafkaConsumerConfig.DEFAULT_KAFKA_CONSUMER,
    )
    fun handleWorkspaceCacheEviction(
        @Payload record: ConsumerRecord<String, String>,
        @Headers headers: Map<String, Any>,
    ) = runBlocking {
        val event = record.value().toObject(EventRecord::class.java)
            ?: throw IllegalArgumentException("Record can't be deserialize, record: $record")

        if (event.eventAction == EventAction.CREATED) {
            return@runBlocking
        }

        val payload = event.payload.toJson().toObject(WorkspaceEvent::class.java)
            ?: throw IllegalArgumentException("Record Payload can't be deserialize, record: $record")

        withContext(dispatcher) {
            workspaceLocalCacheEvictManager.evict(
                workspaceId = payload.workspaceId,
            )
        }
    }

    @DltHandler
    fun dltHandler(
        @Payload record: ConsumerRecord<String, String>,
        @Headers headers: Map<String, Any>,
    ) = runBlocking {
        log.error {
            """
            Workspace Cache Evict Consumer DLT is Received
            - record=$record
            - headers=$headers
            """.trimIndent()
        }
    }

    companion object {
        private const val GROUP_ID = "workspace-cache-evict-consumer"
    }

}
