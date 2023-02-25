package com.story.platform.apiconsumer.domain.subscription

import com.story.platform.core.common.utils.JsonUtils
import com.story.platform.core.common.utils.LoggerUtilsExtension.log
import com.story.platform.core.domain.subscription.SubscriptionEvent
import com.story.platform.core.domain.subscription.SubscriptionEventType
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class SubscriptionConsumer {

    @KafkaListener(topics = ["\${story.kafka.topic.subscription}"], groupId = "SUBSCRIPTION")
    fun handleSubscriptionEvent(record: ConsumerRecord<String, String>) {
        val event = JsonUtils.toObject(record.value(), SubscriptionEvent::class.java)
            ?: throw IllegalArgumentException("Record can't be deserialize, record: $record")

        when (event.eventType) {
            SubscriptionEventType.UPSERT -> log.info("Subscription Upsert Event: $event")
            SubscriptionEventType.DELETE -> log.info("Subscription Delete Event: $event")
        }
    }

}
