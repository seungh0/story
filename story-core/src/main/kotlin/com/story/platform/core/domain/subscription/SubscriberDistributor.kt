package com.story.platform.core.domain.subscription

import com.story.platform.core.common.coroutine.IOBound
import com.story.platform.core.common.json.toJson
import com.story.platform.core.infrastructure.kafka.KafkaProducerConfig
import com.story.platform.core.infrastructure.kafka.TopicType
import com.story.platform.core.infrastructure.kafka.send
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

        withContext(dispatcher) {
            for (slot in SubscriptionSlotAssigner.FIRST_SLOT_ID..lastSlot) {
                launch {
                    val event = SubscriberDistributedEvent(
                        workspaceId = workspaceId,
                        componentId = componentId,
                        targetId = targetId,
                        slot = slot,
                    )

                    kafkaTemplate.send(
                        topicType = TopicType.SUBSCRIBER_DISTRIBUTOR,
                        data = event.toJson()
                    )
                }
            }
        }
    }

}
