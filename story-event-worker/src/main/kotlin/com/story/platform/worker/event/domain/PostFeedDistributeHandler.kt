package com.story.platform.worker.event.domain

import com.story.platform.core.common.spring.HandlerAdapter
import com.story.platform.core.domain.event.EventAction
import com.story.platform.core.domain.feed.mapping.FeedMappingRetriever
import com.story.platform.core.domain.post.PostEvent
import com.story.platform.core.domain.subscription.SubscriberDistributor

@HandlerAdapter
class PostFeedDistributeHandler(
    private val feedMappingRetriever: FeedMappingRetriever,
    private val subscriberDistributor: SubscriberDistributor,
) {

    suspend fun distributePostFeeds(payload: PostEvent, eventId: Long, eventAction: EventAction, eventKey: String) {
        val feedMappings = feedMappingRetriever.listConnectedFeedMappings(
            workspaceId = payload.workspaceId,
            sourceResourceId = payload.resourceId,
            sourceComponentId = payload.componentId,
        )

        if (feedMappings.isEmpty()) {
            return
        }

        feedMappings.forEach { feedMapping ->
            subscriberDistributor.distribute(
                workspaceId = payload.workspaceId,
                feedComponentId = feedMapping.feedComponentId,
                sourceResourceId = feedMapping.sourceResourceId,
                sourceComponentId = feedMapping.sourceComponentId,
                subscriptionComponentId = feedMapping.subscriptionComponentId,
                targetId = payload.accountId,
                payload = payload,
                eventId = eventId,
                eventAction = eventAction,
                eventKey = eventKey,
            )
        }
    }

}
