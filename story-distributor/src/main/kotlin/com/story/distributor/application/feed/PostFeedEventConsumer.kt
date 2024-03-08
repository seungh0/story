package com.story.distributor.application.feed

import com.story.core.common.annotation.EventConsumer
import com.story.core.common.annotation.IOBound
import com.story.core.common.json.toJson
import com.story.core.common.json.toObject
import com.story.core.domain.event.EventRecord
import com.story.core.domain.post.PostEvent
import com.story.core.infrastructure.kafka.KafkaConsumerConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload

@EventConsumer
class PostFeedEventConsumer(
    @IOBound
    private val dispatcher: CoroutineDispatcher,
    private val postFeedDistributedEventHandlerFinder: PostFeedActionHandlerFinder,
) {

    @KafkaListener(
        topics = ["\${story.kafka.topic.post.name}"],
        groupId = "post-feed-distribute-event-consumer",
        containerFactory = KafkaConsumerConfig.DEFAULT_BATCH_KAFKA_CONSUMER,
    )
    fun handlePostFeedEvent(
        @Payload records: List<ConsumerRecord<String, String>>,
    ) = runBlocking {
        records.chunked(MAX_PARALLEL_COUNT).forEach { chunkedRecords ->
            chunkedRecords.map { record ->
                withContext(dispatcher) {
                    launch {
                        val event = record.value().toObject(EventRecord::class.java)
                            ?: throw IllegalArgumentException("Record can't be deserialize, record: $record")

                        val handler = postFeedDistributedEventHandlerFinder[event.eventAction]
                            ?: return@launch

                        val payload = event.payload.toJson().toObject(PostEvent::class.java)
                            ?: throw IllegalArgumentException("Record Payload can't be deserialize, record: $record")

                        handler.handle(event = event, payload = payload)
                    }
                }
            }.joinAll()
        }
    }

    companion object {
        private const val MAX_PARALLEL_COUNT = 5
    }

}
