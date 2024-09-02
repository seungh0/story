package com.story.distributor.application.feed

import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.event.EventAction
import com.story.core.domain.event.EventRecord
import com.story.core.domain.feed.FeedFanoutMessage
import com.story.core.domain.feed.FeedFanoutMessageProducer
import com.story.core.domain.feed.FeedItem
import com.story.core.domain.feed.FeedOptions
import com.story.core.domain.feed.mapping.FeedMappingReaderWithCache
import com.story.core.domain.post.PostEvent
import com.story.core.domain.post.PostEventKey
import com.story.core.domain.resource.ResourceId
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
        eventAction: EventAction,
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
                                    event = EventRecord(
                                        eventAction = EventAction.CREATED,
                                        eventKey = PostEventKey(
                                            spaceId = payload.spaceId,
                                            postId = payload.postId
                                        ).makeKey(),
                                        payload = FeedFanoutMessage(
                                            workspaceId = feedMapping.workspaceId,
                                            componentId = feedMapping.feedComponentId,
                                            options = FeedOptions(
                                                retention = feedMapping.retention,
                                            ),
                                            item = FeedItem(
                                                resourceId = ResourceId.POSTS,
                                                componentId = payload.componentId,
                                                itemId = payload.postId.serialize(),
                                            ),
                                            slotId = slotId,
                                            targetId = payload.ownerId,
                                            subscriptionComponentId = feedMapping.subscriptionComponentId,
                                        )
                                    )
                                )
                            }
                        }.joinAll()
                    }
            }
        }

}
