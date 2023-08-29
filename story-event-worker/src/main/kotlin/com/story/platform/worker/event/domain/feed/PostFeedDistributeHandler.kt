package com.story.platform.worker.event.domain.feed

import com.story.platform.core.common.spring.HandlerAdapter
import com.story.platform.core.domain.event.EventAction
import com.story.platform.core.domain.feed.FeedEvent
import com.story.platform.core.domain.feed.FeedEventProducer
import com.story.platform.core.domain.feed.mapping.FeedMappingRetriever
import com.story.platform.core.domain.post.PostEvent
import com.story.platform.core.domain.subscription.SubscriberSequenceGenerator
import com.story.platform.core.domain.subscription.SubscriptionSlotAssigner
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@HandlerAdapter
class PostFeedDistributeHandler(
    private val feedMappingRetriever: FeedMappingRetriever,
    private val subscriberSequenceGenerator: SubscriberSequenceGenerator,
    private val feedEventProducer: FeedEventProducer,
) {

    suspend fun distributePostFeeds(
        payload: PostEvent,
        eventId: Long,
        eventAction: EventAction,
        eventKey: String,
        parallelCount: Int = 5,
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
                    targetId = payload.accountId,
                )

                if (subscriberSequences <= 0) {
                    return@coroutineScope
                }

                // TODO: 나중에 구독자 갯수로 헤비 유저들의 경우 다른 방법으로 분산시키는 것도 좋을듯...

                LongRange(
                    start = SubscriptionSlotAssigner.FIRST_SLOT_ID,
                    endInclusive = SubscriptionSlotAssigner.assign(sequence = subscriberSequences)
                )
                    .chunked(size = parallelCount)
                    .forEach { chunkedSlotIds ->
                        chunkedSlotIds.map { slotId ->
                            launch {
                                feedEventProducer.publishEvent(
                                    event = FeedEvent.of(
                                        eventAction = eventAction,
                                        eventKey = eventKey,
                                        workspaceId = feedMapping.workspaceId,
                                        feedComponentId = feedMapping.feedComponentId,
                                        subscriptionComponentId = feedMapping.subscriptionComponentId,
                                        sourceResourceId = feedMapping.sourceResourceId,
                                        sourceComponentId = feedMapping.sourceComponentId,
                                        targetId = payload.accountId,
                                        slotId = slotId,
                                        payload = payload,
                                    )
                                )
                            }
                        }
                    }
            }
        }

}
