package com.story.platform.apiconsumer.domain.consumer

import com.story.platform.core.common.utils.JsonUtils
import com.story.platform.core.domain.subscription.SubscriptionEvent
import com.story.platform.core.domain.subscription.SubscriptionSubscriber
import com.story.platform.core.domain.subscription.SubscriptionUnSubscriber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class SubscriptionConsumer(
    private val subscriptionSubscriber: SubscriptionSubscriber,
    private val subscriptionUnSubscriber: SubscriptionUnSubscriber,
) {

    @KafkaListener(topics = ["\${story.kafka.topic.subscription}"], groupId = "SUBSCRIPTION")
    fun handleSubscription(record: ConsumerRecord<String, String>) {
        runBlocking {
            withContext(Dispatchers.IO) {
                launch {
                    val event = JsonUtils.toObject(record.value(), SubscriptionEvent::class.java)
                        ?: throw IllegalArgumentException("Record can't be deserialize, record: $record")

                    subscriptionSubscriber.subscribe(
                        serviceType = event.serviceType,
                        subscriptionType = event.subscriptionType,
                        subscriberId = event.subscriberId,
                        targetId = event.targetId,
                    )
                }.join()
            }
        }
    }

    @KafkaListener(topics = ["\${story.kafka.topic.unsubscription}"], groupId = "UNSUBSCRIPTION")
    fun handleUnsubscription(record: ConsumerRecord<String, String>) {
        runBlocking {
            withContext(Dispatchers.IO) {
                launch {
                    val event = JsonUtils.toObject(record.value(), SubscriptionEvent::class.java)
                        ?: throw IllegalArgumentException("Record can't be deserialize, record: $record")

                    subscriptionUnSubscriber.unsubscribe(
                        serviceType = event.serviceType,
                        subscriptionType = event.subscriptionType,
                        subscriberId = event.subscriberId,
                        targetId = event.targetId,
                    )
                }.join()
            }
        }
    }

}
