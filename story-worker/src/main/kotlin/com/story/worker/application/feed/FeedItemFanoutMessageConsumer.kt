package com.story.worker.application.feed

import com.story.core.common.annotation.EventConsumer
import com.story.core.common.annotation.IOBound
import com.story.core.common.json.toJson
import com.story.core.common.json.toObject
import com.story.core.domain.event.EventRecord
import com.story.core.domain.feed.FeedFanoutMessage
import com.story.core.support.kafka.KafkaConsumerConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload

@EventConsumer
class FeedItemFanoutMessageConsumer(
    @IOBound
    private val dispatcher: CoroutineDispatcher,
    private val feedFanoutHandlerFinder: FeedItemFanoutActionHandlerFinder,
) {

    @KafkaListener(
        topics = ["\${story.kafka.topic.feed-fanout.name}"],
        groupId = GROUP_ID,
        containerFactory = KafkaConsumerConfig.DEFAULT_BATCH_KAFKA_CONSUMER,
    )
    fun handleFeedFanoutMessage(
        @Payload records: List<ConsumerRecord<String, String>>,
    ) = runBlocking {
        records.chunked(MAX_PARALLEL_COUNT)
            .forEach { chunkedRecords ->
                chunkedRecords.map { record ->
                    withContext(dispatcher) {
                        launch {
                            val message = record.value().toObject(EventRecord::class.java)
                                ?: throw IllegalArgumentException("Record can't be deserialize, record: $records")

                            val handler = feedFanoutHandlerFinder.get(eventAction = message.eventAction)
                                ?: return@launch

                            val payload = message.payload.toJson().toObject(FeedFanoutMessage::class.java)
                                ?: throw IllegalArgumentException("Record Payload can't be deserialize, record: $records")

                            handler.handle(record = message, payload = payload)
                        }
                    }
                }.joinAll()
            }
    }

    companion object {
        private const val GROUP_ID = "feed-fanout-message-consumer"
        private const val MAX_PARALLEL_COUNT = 5
    }

}
