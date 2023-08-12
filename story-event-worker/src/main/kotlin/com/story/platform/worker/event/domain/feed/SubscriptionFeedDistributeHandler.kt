package com.story.platform.worker.event.domain.feed

import com.story.platform.core.common.json.toJson
import com.story.platform.core.common.spring.HandlerAdapter
import com.story.platform.core.domain.event.EventAction
import com.story.platform.core.domain.feed.FeedEvent
import com.story.platform.core.domain.feed.mapping.FeedMappingRetriever
import com.story.platform.core.domain.subscription.SubscriberSequenceGenerator
import com.story.platform.core.domain.subscription.SubscriptionEvent
import com.story.platform.core.domain.subscription.SubscriptionSlotAssigner
import com.story.platform.core.infrastructure.kafka.KafkaProducerConfig
import com.story.platform.core.infrastructure.kafka.TopicType
import com.story.platform.core.infrastructure.kafka.send
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate

@HandlerAdapter
class SubscriptionFeedDistributeHandler(
    private val feedMappingRetriever: FeedMappingRetriever,
    private val subscriberSequenceGenerator: SubscriberSequenceGenerator,

    @Qualifier(KafkaProducerConfig.DEFAULT_KAFKA_TEMPLATE)
    private val kafkaTemplate: KafkaTemplate<String, String>,
) {

    suspend fun distributePostFeeds(
        payload: SubscriptionEvent,
        eventId: Long,
        eventAction: EventAction,
        eventKey: String,
    ) =
        coroutineScope {
            val feedMappings = feedMappingRetriever.listConnectedFeedMappings(
                workspaceId = payload.workspaceId,
                sourceResourceId = payload.resourceId,
                sourceComponentId = payload.componentId,
            )

            if (feedMappings.isEmpty()) {
                return@coroutineScope
            }

            feedMappings.forEach { feedMapping ->
                val subscriberSequences = subscriberSequenceGenerator.lastSequence(
                    workspaceId = feedMapping.workspaceId,
                    componentId = feedMapping.subscriptionComponentId,
                    targetId = payload.subscriberId,
                )

                if (subscriberSequences <= 0) {
                    return@coroutineScope
                }

                // TODO: 나중에 구독자 갯수로 헤비 유저들의 경우 다른 방법으로 분산시키는 것도 좋을듯...

                LongRange(
                    start = SubscriptionSlotAssigner.FIRST_SLOT_ID,
                    endInclusive = SubscriptionSlotAssigner.assign(sequence = subscriberSequences)
                )
                    .chunked(size = 5)
                    .forEach { chunkedSlots ->
                        chunkedSlots.map { slot ->
                            launch {
                                val event = FeedEvent.of(
                                    eventAction = eventAction,
                                    eventKey = eventKey,
                                    workspaceId = feedMapping.workspaceId,
                                    feedComponentId = feedMapping.feedComponentId,
                                    subscriptionComponentId = feedMapping.subscriptionComponentId,
                                    sourceResourceId = feedMapping.sourceResourceId,
                                    sourceComponentId = feedMapping.sourceComponentId,
                                    targetId = payload.subscriberId,
                                    slotId = slot,
                                    payload = payload,
                                )

                                kafkaTemplate.send(
                                    topicType = TopicType.FEED,
                                    data = event.toJson()
                                )
                            }
                        }
                    }
            }
        }

}
