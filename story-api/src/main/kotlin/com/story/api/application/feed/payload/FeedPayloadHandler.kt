package com.story.api.application.feed.payload

import com.story.core.domain.feed.Feed
import com.story.core.domain.feed.FeedPayload
import com.story.core.domain.resource.ResourceId

interface FeedPayloadHandler {

    fun resourceId(): ResourceId

    suspend fun handle(
        workspaceId: String,
        feeds: Collection<Feed>,
        requestUserId: String?,
    ): Map<Long, FeedPayload>

}
