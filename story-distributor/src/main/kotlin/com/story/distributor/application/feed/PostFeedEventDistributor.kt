package com.story.distributor.application.feed

import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.event.EventAction
import com.story.core.domain.feed.FeedFanoutMessage
import com.story.core.domain.feed.FeedFanoutMessageProducer
import com.story.core.domain.feed.mapping.FeedMappingReaderWithCache
import com.story.core.domain.post.PostEvent
import com.story.core.domain.subscription.SubscriberSequenceRepository
import com.story.core.domain.subscription.SubscriptionSlotAssigner.FIRST_SLOT_ID
import com.story.core.domain.subscription.SubscriptionSlotAssigner.assign
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

@HandlerAdapter
class PostFeedEventDistributor(
    private val feedMappingReaderWithCache: FeedMappingReaderWithCache,
    private val subscriberSequenceRepository: SubscriberSequenceRepository,
    private val feedFanoutMessageProducer: FeedFanoutMessageProducer,
) {

    suspend fun distribute(
        payload: PostEvent,
        eventId: Long,
        eventAction: EventAction,
        eventKey: String,
        parallelCount: Int = 5,
    ) =
        coroutineScope {
            val feedMappings = feedMappingReaderWithCache.listConnectedFeedMappings(
                workspaceId = payload.workspaceId,
                sourceResourceId = payload.resourceId,
                sourceComponentId = payload.componentId,
            )

            if (feedMappings.isEmpty()) {
                return@coroutineScope
            }

            feedMappings.forEach { feedMapping ->
                val subscriberCount = subscriberSequenceRepository.getLastSequence(
                    workspaceId = feedMapping.workspaceId,
                    componentId = feedMapping.subscriptionComponentId,
                    targetId = payload.ownerId,
                )

                if (subscriberCount <= 0) {
                    return@coroutineScope
                }

                // TODO: 나중에 구독자 갯수로 헤비 유저들의 경우 다른 방법으로 분산시키는 것도 좋을듯...

                LongRange(start = FIRST_SLOT_ID, endInclusive = assign(sequence = subscriberCount))
                    .chunked(size = parallelCount)
                    .forEach { chunkedSlotIds ->
                        chunkedSlotIds.map { slotId ->
                            launch {
                                feedFanoutMessageProducer.publish(
                                    event = FeedFanoutMessage.of(
                                        eventAction = eventAction,
                                        eventKey = eventKey,
                                        workspaceId = feedMapping.workspaceId,
                                        feedComponentId = feedMapping.feedComponentId,
                                        subscriptionComponentId = feedMapping.subscriptionComponentId,
                                        sourceResourceId = feedMapping.sourceResourceId,
                                        sourceComponentId = feedMapping.sourceComponentId,
                                        targetId = payload.ownerId,
                                        slotId = slotId,
                                        payload = payload,
                                        retention = feedMapping.retention,
                                    )
                                )
                            }
                        }.joinAll()
                    }
            }
        }

}
