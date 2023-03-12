package com.story.platform.apiconsumer.domain.feed

import com.story.platform.core.common.distribution.LargeDistributionKey
import com.story.platform.core.domain.feed.FeedRegister
import com.story.platform.core.domain.feed.FeedRemover
import com.story.platform.core.domain.post.PostEvent
import com.story.platform.core.domain.post.PostEventType
import com.story.platform.core.domain.subscription.SubscriptionEvent
import com.story.platform.core.domain.subscription.SubscriptionEventType
import com.story.platform.core.support.json.JsonUtils
import com.story.platform.core.support.kafka.KafkaConsumerConfig
import kotlinx.coroutines.runBlocking
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class FeedTaskerConsumer(
    private val feedRegister: FeedRegister,
    private val feedRemover: FeedRemover,
) {

    @KafkaListener(
        topics = ["\${story.kafka.post.topic}"],
        groupId = "\${story.kafka.post.group-id}",
        containerFactory = KafkaConsumerConfig.DEFAULT_CONTAINER_FACTORY,
    )
    fun handlePostEvent(record: ConsumerRecord<String, String>) {
        val event = JsonUtils.toObject(record.value(), PostEvent::class.java)
            ?: throw IllegalArgumentException("Record can't be deserialize, record: $record")

        runBlocking {
            when (event.eventType) {
                PostEventType.CREATED -> LargeDistributionKey.ALL_KEYS.map { distributedKey ->
                    feedRegister.registerPostFeed(
                        serviceType = event.serviceType,
                        targetId = event.accountId,
                        distributedKey = distributedKey.key,
                        spaceType = event.spaceType,
                        spaceId = event.spaceId,
                        postId = event.postId,
                    )
                }

                PostEventType.DELETED -> LargeDistributionKey.ALL_KEYS.map { distributedKey ->
                    feedRemover.removePostFeed(
                        serviceType = event.serviceType,
                        targetId = event.accountId,
                        distributedKey = distributedKey.key,
                        spaceType = event.spaceType,
                        spaceId = event.spaceId,
                        postId = event.postId,
                    )
                }

                else -> return@runBlocking
            }
        }
    }


    @KafkaListener(
        topics = ["\${story.kafka.subscription.topic}"],
        groupId = "\${story.kafka.subscription.group-id}",
        containerFactory = KafkaConsumerConfig.DEFAULT_CONTAINER_FACTORY,
    )
    fun handleSubscriptionEvent(record: ConsumerRecord<String, String>) {
        val event = JsonUtils.toObject(record.value(), SubscriptionEvent::class.java)
            ?: throw IllegalArgumentException("Record can't be deserialize, record: $record")

        runBlocking {
            when (event.eventType) {
                SubscriptionEventType.UPSERT -> LargeDistributionKey.ALL_KEYS.map { distributedKey ->
                    feedRegister.registerSubscriptionFeed(
                        serviceType = event.serviceType,
                        subscriptionType = event.subscriptionType,
                        distributedKey = distributedKey.key,
                        targetId = event.targetId,
                        subscriberId = event.subscriberId,
                    )
                }

                SubscriptionEventType.DELETE -> LargeDistributionKey.ALL_KEYS.map { distributedKey ->
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
