package com.story.platform.executor.domain.feed

import com.story.platform.core.common.coroutine.IOBound
import com.story.platform.core.common.json.toObject
import com.story.platform.core.common.spring.EventConsumer
import com.story.platform.core.domain.subscription.SubscriberDistributedEvent
import com.story.platform.core.infrastructure.kafka.KafkaConsumerConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Headers
import org.springframework.messaging.handler.annotation.Payload

@EventConsumer
class FeedExecutor(
    @IOBound
    private val dispatcher: CoroutineDispatcher,
    private val feedExecutorHandler: FeedExecutorHandler,
) {

    @KafkaListener(
        topics = ["\${story.kafka.topic.feed.executor}"],
        groupId = GROUP_ID,
        containerFactory = KafkaConsumerConfig.POST_CONTAINER_FACTORY,
    )
    fun handleFeedExecutor(
        @Payload record: ConsumerRecord<String, String>,
        @Headers headers: Map<String, Any>,
    ) = runBlocking {
        val payload = record.value().toObject(SubscriberDistributedEvent::class.java)
            ?: throw IllegalArgumentException("Record can't be deserialize, record: $record")

        withContext(dispatcher) {
            feedExecutorHandler.publishFeeds(payload = payload)
        }
    }

    companion object {
        private const val GROUP_ID = "feed-event-executor"
    }

}
