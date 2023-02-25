package com.story.platform.apiconsumer.domain.subscription

import com.story.platform.core.common.utils.JsonUtils
import com.story.platform.core.domain.subscription.SubscriptionEvent
import com.story.platform.core.domain.subscription.SubscriptionEventType
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
    fun handleSubscriptionEvent(record: ConsumerRecord<String, String>) {
        val event = JsonUtils.toObject(record.value(), SubscriptionEvent::class.java)
            ?: throw IllegalArgumentException("Record can't be deserialize, record: $record")

        runBlocking {
            withContext(Dispatchers.IO) {
                launch {
                    when (event.type) {
                        SubscriptionEventType.UPSERT -> subscriptionSubscriber.subscribe(
                            serviceType = event.serviceType,
                            subscriptionType = event.subscriptionType,
                            subscriberId = event.subscriberId,
                            targetId = event.targetId,
                        )

                        SubscriptionEventType.DELETE -> subscriptionUnSubscriber.unsubscribe(
                            serviceType = event.serviceType,
                            subscriptionType = event.subscriptionType,
                            subscriberId = event.subscriberId,
                            targetId = event.targetId,
                        )
                    }
                }.join()
            }
        }
    }

}
