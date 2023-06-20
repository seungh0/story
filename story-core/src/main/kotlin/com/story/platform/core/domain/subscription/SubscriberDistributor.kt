package com.story.platform.core.domain.subscription

import com.story.platform.core.common.error.InternalServerException
import com.story.platform.core.infrastructure.kafka.KafkaProducerConfig
import com.story.platform.core.infrastructure.kafka.KafkaTopicFinder
import com.story.platform.core.infrastructure.kafka.TopicType
import com.story.platform.core.support.coroutine.CoroutineConfig.Companion.DEFAULT_TIMEOUT_MS
import com.story.platform.core.support.coroutine.IOBound
import com.story.platform.core.support.json.toJson
import kotlinx.coroutines.CoroutineDispatcher
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

    @IOBound
    private val dispatcher: CoroutineDispatcher,
) {

    suspend fun distribute(
        workspaceId: String,
        componentId: String,
        targetId: String,
    ) {
        val subscribersCount = subscribersCountRepository.get(
            key = SubscribersCountKey(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
            )
        )

        val lastSlot = SubscriptionSlotAssigner.assign(sequence = subscribersCount)

        for (slot in SubscriptionSlotAssigner.FIRST_SLOT_ID..lastSlot) {
            withContext(dispatcher) {
                launch {
                    withTimeout(DEFAULT_TIMEOUT_MS) {
                        try {
                            val event = SubscriberDistributedEvent(
                                workspaceId = workspaceId,
                                componentId = componentId,
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
