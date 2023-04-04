package com.story.platform.apiconsumer.domain.feed

import com.story.platform.core.domain.feed.FeedRegister
import com.story.platform.core.domain.feed.FeedRemover
import com.story.platform.core.domain.post.PostEvent
import com.story.platform.core.domain.post.PostEventType
import com.story.platform.core.domain.subscription.SubscriberDistributedKeyGenerator
import com.story.platform.core.domain.subscription.SubscriptionEvent
import com.story.platform.core.domain.subscription.SubscriptionEventType
import com.story.platform.core.infrastructure.kafka.KafkaConsumerConfig
import com.story.platform.core.support.json.JsonUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Headers
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service

@Service
class FeedHandlerConsumer(
    private val feedRegister: FeedRegister,
    private val feedRemover: FeedRemover,
) {

    @KafkaListener(
        topics = ["\${story.kafka.post.topic}"],
        groupId = "\${story.kafka.post.group-id}",
        containerFactory = KafkaConsumerConfig.POST_CONTAINER_FACTORY,
    )
    fun handlePostFeed(
        @Payload record: ConsumerRecord<String, String>,
        @Headers headers: Map<String, Any>,
    ) {
        val event = JsonUtils.toObject(record.value(), PostEvent::class.java)
            ?: throw IllegalArgumentException("Record can't be deserialize, record: $record")

        runBlocking {
            withContext(Dispatchers.IO) {
                when (event.eventType) {
                    PostEventType.CREATED -> SubscriberDistributedKeyGenerator.KEYS.map { distributedKey ->
                        feedRegister.addPostFeed(
                            serviceType = event.serviceType,
                            targetId = event.accountId,
                            distributedKey = distributedKey.key,
                            spaceType = event.spaceType,
                            spaceId = event.spaceId,
                            postId = event.postId,
                        )
                    }

                    PostEventType.DELETED -> SubscriberDistributedKeyGenerator.KEYS.map { distributedKey ->
                        feedRemover.removePostFeed(
                            serviceType = event.serviceType,
                            targetId = event.accountId,
                            distributedKey = distributedKey.key,
                            spaceType = event.spaceType,
                            spaceId = event.spaceId,
                            postId = event.postId,
                        )
                    }

                    else -> return@withContext
                }
            }
        }
    }

    @KafkaListener(
        topics = ["\${story.kafka.subscription.topic}"],
        groupId = "\${story.kafka.subscription.group-id}",
        containerFactory = KafkaConsumerConfig.SUBSCRIPTION_CONTAINER_FACTORY,
    )
    fun handleSubscriptionFeed(
        @Payload record: ConsumerRecord<String, String>,
        @Headers headers: Map<String, Any>,
    ) {
        val event = JsonUtils.toObject(record.value(), SubscriptionEvent::class.java)
            ?: throw IllegalArgumentException("Record can't be deserialize, record: $record")

        runBlocking {
            withContext(Dispatchers.IO) {
                when (event.eventType) {
                    SubscriptionEventType.UPSERT -> SubscriberDistributedKeyGenerator.KEYS.map { distributedKey ->
                        feedRegister.addSubscriptionFeed(
                            serviceType = event.serviceType,
                            subscriptionType = event.subscriptionType,
                            distributedKey = distributedKey.key,
                            targetId = event.targetId,
                            subscriberId = event.subscriberId,
                        )
                    }

                    SubscriptionEventType.DELETE -> SubscriberDistributedKeyGenerator.KEYS.map { distributedKey ->
                        feedRemover.removeSubscriptionFeed(
                            serviceType = event.serviceType,
                            subscriptionType = event.subscriptionType,
                            distributedKey = distributedKey.key,
                            targetId = event.targetId,
                            subscriberId = event.subscriberId,
                        )
                    }
                }
            }
        }
    }

}
