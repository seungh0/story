package com.story.platform.apiconsumer.domain.subscription

import com.story.platform.core.common.utils.LoggerUtilsExtension.log
import com.story.platform.core.domain.subscription.SubscriptionEvent
import com.story.platform.core.domain.subscription.SubscriptionEventType
import com.story.platform.core.support.json.JsonUtils
import com.story.platform.core.support.kafka.KafkaConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class SubscriptionConsumer {

    @KafkaListener(
        topics = ["\${story.kafka.subscription.topic}"],
        groupId = "\${story.kafka.subscription.group-id}",
        containerFactory = KafkaConsumerConfig.DEFAULT_CONTAINER_FACTORY,
    )
    fun handleSubscriptionEvent(record: ConsumerRecord<String, String>) {
        val event = JsonUtils.toObject(record.value(), SubscriptionEvent::class.java)
            ?: throw IllegalArgumentException("Record can't be deserialize, record: $record")

        when (event.eventType) {
            SubscriptionEventType.UPSERT -> log.info("Subscription Upsert Event: $event")
            SubscriptionEventType.DELETE -> log.info("Subscription Delete Event: $event")
        }
    }

}
