package com.story.distributor.application.feed.mapping

import com.story.core.common.annotation.EventConsumer
import com.story.core.common.annotation.IOBound
import com.story.core.common.json.toJson
import com.story.core.common.json.toObject
import com.story.core.domain.event.EventRecord
import com.story.core.domain.feed.mapping.FeedMappingEvent
import com.story.core.domain.feed.mapping.FeedMappingLocalCacheEvictManager
import com.story.core.support.kafka.KafkaConsumerConfig
import com.story.core.support.kafka.RetryableKafkaListener
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.messaging.handler.annotation.Headers
import org.springframework.messaging.handler.annotation.Payload

@EventConsumer
class FeedMappingCacheEvictConsumer(
    private val feedMappingLocalCacheEvictManager: FeedMappingLocalCacheEvictManager,

    @IOBound
    private val dispatcher: CoroutineDispatcher,
) {

    @RetryableKafkaListener(
        topics = ["\${story.kafka.topic.feed-mapping.name}"],
        groupId = "$GROUP_ID-\${random.uuid}",
        containerFactory = KafkaConsumerConfig.DEFAULT_KAFKA_CONSUMER,
    )
    fun handleFeedMappingCacheEviction(
        @Payload record: ConsumerRecord<String, String>,
        @Headers headers: Map<String, Any>,
    ) = runBlocking {
        val event = record.value().toObject(EventRecord::class.java)
            ?: throw IllegalArgumentException("Record can't be deserialize, record: $record")

        val payload = event.payload.toJson().toObject(FeedMappingEvent::class.java)
            ?: throw IllegalArgumentException("Record Payload can't be deserialize, record: $record")

        withContext(dispatcher) {
            feedMappingLocalCacheEvictManager.evictFeedMapping(
                workspaceId = payload.workspaceId,
                sourceResourceId = payload.sourceResourceId,
                sourceComponentId = payload.sourceComponentId,
            )
        }
    }

    companion object {
        private const val GROUP_ID = "feed-mapping-cache-evict-consumer"
    }

}
