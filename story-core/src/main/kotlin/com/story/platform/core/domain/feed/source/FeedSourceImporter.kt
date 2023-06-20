package com.story.platform.core.domain.feed.source

import com.story.platform.core.domain.feed.FeedMessage
import com.story.platform.core.domain.feed.FeedSourceType

interface FeedSourceImporter {

    fun sourceType(): FeedSourceType

    suspend fun fetch(workspaceId: String, componentId: String, sourceIds: Collection<String>): List<FeedMessage>

}
