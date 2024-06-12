package com.story.core.domain.workspace

import com.story.core.common.annotation.EventProducer
import com.story.core.common.annotation.IOBound
import com.story.core.common.json.toJson
import com.story.core.support.kafka.KafkaProducerConfig
import com.story.core.support.kafka.KafkaRecordKeyGenerator
import com.story.core.support.kafka.KafkaTopic
import com.story.core.support.kafka.send
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate

@EventProducer
class WorkspaceEventProducer(
    @Qualifier(KafkaProducerConfig.DEFAULT_ACK_ALL_KAFKA_PRODUCER)
    private val kafkaTemplate: KafkaTemplate<String, String>,

    @IOBound
    private val dispatcher: CoroutineDispatcher,
) {

    suspend fun publishDeletedEvent(
        workspaceId: String,
    ) {
        withContext(dispatcher) {
            kafkaTemplate.send(
                kafkaTopic = KafkaTopic.WORKSPACE,
                key = KafkaRecordKeyGenerator.workspace(workspaceId),
                data = WorkspaceEvent.deleted(workspaceId = workspaceId).toJson(),
            )
        }
    }

}
