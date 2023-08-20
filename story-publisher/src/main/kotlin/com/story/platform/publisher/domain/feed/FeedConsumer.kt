package com.story.platform.publisher.domain.feed

import com.story.platform.core.common.coroutine.IOBound
import com.story.platform.core.common.json.toJson
import com.story.platform.core.common.json.toObject
import com.story.platform.core.common.spring.EventConsumer
import com.story.platform.core.domain.event.EventRecord
import com.story.platform.core.domain.feed.FeedEvent
import com.story.platform.core.infrastructure.kafka.KafkaConsumerConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload

@EventConsumer
class FeedConsumer(
    @IOBound
    private val dispatcher: CoroutineDispatcher,
    private val feedHandlerManager: FeedHandlerManager,
) {

    @KafkaListener(
        topics = ["\${story.kafka.topic.feed}"],
        groupId = GROUP_ID,
        containerFactory = KafkaConsumerConfig.DEFAULT_BATCH_KAFKA_CONSUMER,
    )
    fun handleFeedExecutor(
        @Payload records: List<ConsumerRecord<String, String>>,
    ) = runBlocking {
        records.chunked(MAX_PARALLEL_COUNT).map { chunkedRecords ->
            chunkedRecords.map { record ->
                launch {
                    val event = record.value().toObject(EventRecord::class.java)
                        ?: throw IllegalArgumentException("Record can't be deserialize, record: $records")

                    val payload = event.payload.toJson().toObject(FeedEvent::class.java)
                        ?: throw IllegalArgumentException("Record Payload can't be deserialize, record: $records")

                    withContext(dispatcher) {
                        feedHandlerManager.handle(event = event, payload = payload)
                    }
                }
            }.joinAll()
        }
    }

    companion object {
        private const val GROUP_ID = "feed-event-publisher"
        private const val MAX_PARALLEL_COUNT = 5
    }

}
