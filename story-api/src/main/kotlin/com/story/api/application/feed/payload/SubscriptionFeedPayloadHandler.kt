package com.story.api.application.feed.payload

import com.story.api.application.subscription.SubscriptionResponse
import com.story.core.domain.feed.Feed
import com.story.core.domain.feed.FeedItem
import com.story.core.domain.feed.FeedPayload
import com.story.core.domain.resource.ResourceId
import com.story.core.domain.subscription.SubscriptionEventKey
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service

@Service
class SubscriptionFeedPayloadHandler : FeedPayloadHandler {

    override fun resourceId(): ResourceId = ResourceId.SUBSCRIPTIONS

    override suspend fun handle(
        workspaceId: String,
        feeds: Collection<Feed>,
        requestUserId: String?,
    ): Map<FeedItem, FeedPayload> = coroutineScope {
        return@coroutineScope feeds.map { feed ->
            async {
                val eventKey = SubscriptionEventKey.parse(feed.item.itemId)
                feed.item to SubscriptionResponse(
                    subscriberId = eventKey.subscriberId,
                    targetId = eventKey.targetId,
                )
            }
        }.awaitAll().associate { it.first to it.second }
    }

}
