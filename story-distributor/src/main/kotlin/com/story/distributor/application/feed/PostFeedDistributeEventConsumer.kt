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
class PostFeedDistributeEventConsumer(
    @IOBound
    private val dispatcher: CoroutineDispatcher,
    private val postFeedDistributeHandler: PostFeedDistributeHandler,
) {

    @KafkaListener(
        topics = ["\${story.kafka.topic.post.name}"],
        groupId = GROUP_ID,
        containerFactory = KafkaConsumerConfig.DEFAULT_BATCH_KAFKA_CONSUMER,
    )
    fun handlePostFeedEvent(
        @Payload records: List<ConsumerRecord<String, String>>,
    ) = runBlocking {
        for (chunkedRecords in records.chunked(MAX_PARALLEL_COUNT)) {
            chunkedRecords.map { record ->
                launch {
                    val event = record.value().toObject(EventRecord::class.java)
                        ?: throw IllegalArgumentException("Record can't be deserialize, record: $record")

                    val payload = event.payload.toJson().toObject(PostEvent::class.java)
                        ?: throw IllegalArgumentException("Record Payload can't be deserialize, record: $record")

                    withContext(dispatcher) {
                        postFeedDistributeHandler.distributePostFeeds(
                            payload = payload,
                            eventId = event.eventId,
                            eventAction = event.eventAction,
                            eventKey = event.eventKey,
                        )
                    }
                }
            }.joinAll()
        }
    }

    companion object {
        private const val GROUP_ID = "post-feed-event-consumer"
        private const val MAX_PARALLEL_COUNT = 5
    }

}
