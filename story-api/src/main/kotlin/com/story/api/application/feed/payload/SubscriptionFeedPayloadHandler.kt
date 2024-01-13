package com.story.api.application.feed.payload

import com.story.api.application.subscription.SubscriptionApiResponse
import com.story.core.domain.event.EventKeyGenerator
import com.story.core.domain.feed.FeedPayload
import com.story.core.domain.feed.FeedResponse
import com.story.core.domain.resource.ResourceId
import org.springframework.stereotype.Service

@Service
class SubscriptionFeedPayloadHandler : FeedPayloadHandler {

    override fun resourceId(): ResourceId = ResourceId.SUBSCRIPTIONS

    override suspend fun handle(
        workspaceId: String,
        feeds: Collection<FeedResponse>,
        requestAccountId: String?,
    ): Map<Long, FeedPayload> {
        return feeds.map { feed ->
            val (subscriberId, targetId) = EventKeyGenerator.parseSubscription(feed.eventKey)
            feed.feedId to SubscriptionApiResponse(
                subscriberId = subscriberId,
                targetId = targetId,
            )
        }.associate { it.first to it.second }
    }

}
