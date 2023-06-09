package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.common.error.InternalServerException
import com.story.platform.core.infrastructure.kafka.KafkaProducerConfig
import com.story.platform.core.infrastructure.kafka.KafkaTopicFinder
import com.story.platform.core.infrastructure.kafka.TopicType
import com.story.platform.core.support.coroutine.CoroutineConfigConstants
import com.story.platform.core.support.json.toJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class SubscriberDistributor(
    private val subscribersCountRepository: SubscribersCountRepository,

    @Qualifier(KafkaProducerConfig.DEFAULT_KAFKA_TEMPLATE)
    private val kafkaTemplate: KafkaTemplate<String, String>,
) {

    suspend fun distribute(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        targetId: String,
    ) {
        val subscribersCount = subscribersCountRepository.get(
            key = SubscriberCountKey(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
            )
        )

        val lastSlot = SubscriptionSlotAssigner.assign(sequence = subscribersCount)

        for (slot in SubscriptionSlotAssigner.FIRST_SLOT_ID..lastSlot) {
            withContext(Dispatchers.IO) {
                launch {
                    withTimeout(CoroutineConfigConstants.DEFAULT_TIMEOUT_MS) {
                        try {
                            val event = SubscriberDistributedEvent(
                                serviceType = serviceType,
                                subscriptionType = subscriptionType,
                                targetId = targetId,
                                slot = slot,
                            )

                            kafkaTemplate.send(
                                KafkaTopicFinder.getTopicName(TopicType.SUBSCRIBER_DISTRIBUTOR),
                                event.toJson()
                            )
                        } catch (exception: TimeoutCancellationException) {
                            throw InternalServerException(exception.message ?: "", exception)
                        }
                    }
                }
            }
        }
    }

}
