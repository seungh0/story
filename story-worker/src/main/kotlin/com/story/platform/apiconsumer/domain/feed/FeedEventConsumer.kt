package com.story.platform.apiconsumer.domain.feed

import com.story.platform.core.common.model.EventRecord
import com.story.platform.core.domain.post.PostEvent
import com.story.platform.core.domain.subscription.SubscriberDistributor
import com.story.platform.core.domain.subscription.SubscriptionEvent
import com.story.platform.core.domain.subscription.SubscriptionType
import com.story.platform.core.infrastructure.kafka.KafkaConsumerConfig
import com.story.platform.core.support.json.JsonUtils
import com.story.platform.core.support.json.toJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Headers
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service

@Service
class FeedEventConsumer(
    private val subscriberDistributor: SubscriberDistributor,
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val dispatcher = Dispatchers.IO
        .limitedParallelism(parallelism = 100)

    @KafkaListener(
        topics = ["\${story.kafka.post.topic}"],
        groupId = "\${story.kafka.post.group-id}",
        containerFactory = KafkaConsumerConfig.POST_CONTAINER_FACTORY,
    )
    fun handlePostEvent(
        @Payload record: ConsumerRecord<String, String>,
        @Headers headers: Map<String, Any>,
    ) {
        runBlocking {
            val event: EventRecord<*> = JsonUtils.toObject(record.value(), EventRecord::class.java)
                ?: throw IllegalArgumentException("Record can't be deserialize, record: $record")

            val payload = JsonUtils.toObject(event.payload.toJson(), PostEvent::class.java)
                ?: throw IllegalArgumentException("Record Payload can't be deserialize, record: $record")

            withContext(dispatcher) {
                subscriberDistributor.distribute(
                    serviceType = payload.serviceType,
                    subscriptionType = SubscriptionType.FOLLOW,
                    targetId = payload.spaceId,
                )
            }
        }
    }

    @KafkaListener(
        topics = ["\${story.kafka.subscription.topic}"],
        groupId = "\${story.kafka.subscription.group-id}",
        containerFactory = KafkaConsumerConfig.SUBSCRIPTION_CONTAINER_FACTORY,
    )
    fun handleSubscriptionEvent(
        @Payload record: ConsumerRecord<String, String>,
        @Headers headers: Map<String, Any>,
    ) {
        runBlocking {
            val event: EventRecord<*> = JsonUtils.toObject(record.value(), EventRecord::class.java)
                ?: throw IllegalArgumentException("Record can't be deserialize, record: $record")

            val payload = JsonUtils.toObject(event.payload.toJson(), SubscriptionEvent::class.java)
                ?: throw IllegalArgumentException("Record Payload can't be deserialize, record: $record")

            withContext(dispatcher) {
                subscriberDistributor.distribute(
                    serviceType = payload.serviceType,
                    subscriptionType = SubscriptionType.FOLLOW,
                    targetId = payload.subscriberId,
                )
            }
        }
    }

}
