package com.story.platform.core.domain.subscription

import com.story.platform.core.common.coroutine.IOBound
import com.story.platform.core.common.json.toJson
import com.story.platform.core.domain.event.BaseEvent
import com.story.platform.core.domain.event.EventAction
import com.story.platform.core.domain.resource.ResourceId
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
    private val subscriberSequenceGenerator: SubscriberSequenceGenerator,

    @Qualifier(KafkaProducerConfig.DEFAULT_KAFKA_TEMPLATE)
    private val kafkaTemplate: KafkaTemplate<String, String>,

    @IOBound
    private val dispatcher: CoroutineDispatcher,
) {

    suspend fun <T : BaseEvent> distribute(
        workspaceId: String,
        feedComponentId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
        subscriptionComponentId: String,
        targetId: String,
        payload: T,
        eventId: Long,
        eventKey: String,
        eventAction: EventAction,
    ) {
        val subscriberSequences = subscriberSequenceGenerator.lastSequence(
            workspaceId = workspaceId,
            componentId = subscriptionComponentId,
            targetId = targetId,
        )

        if (subscriberSequences <= 0) {
            return
        }

        val lastsSlotId = SubscriptionSlotAssigner.assign(sequence = subscriberSequences)

        // TODO: 나중에 구독자 갯수로 헤비 유저들의 경우 다른 방법으로 분산시키는 것도 좋을듯...

        withContext(dispatcher) {
            LongRange(start = SubscriptionSlotAssigner.FIRST_SLOT_ID, endInclusive = lastsSlotId)
                .chunked(size = 5)
                .forEach { chunkedSlots ->
                    chunkedSlots.map { slot ->
                        launch {
                            val event = SubscriberDistributedEvent.of(
                                workspaceId = workspaceId,
                                feedComponentId = feedComponentId,
                                subscriptionComponentId = subscriptionComponentId,
                                targetId = targetId,
                                slotId = slot,
                                payload = payload,
                                eventAction = eventAction,
                                sourceResourceId = sourceResourceId,
                                sourceComponentId = sourceComponentId,
                                eventKey = eventKey,
                                eventId = eventId,
                            )

                            kafkaTemplate.send(
                                topicType = TopicType.FEED_EXECUTOR,
                                data = event.toJson()
                            )
                        }
                    }
                }
        }
    }

}
