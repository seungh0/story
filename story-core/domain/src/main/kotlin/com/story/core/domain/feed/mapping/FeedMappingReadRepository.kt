package com.story.core.domain.feed.mapping

import com.story.core.domain.resource.ResourceId
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface FeedMappingReadRepository {

    suspend fun existsById(
        workspaceId: String,
        feedComponentId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
        subscriptionComponentId: String,
    ): Boolean

    suspend fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySourceResourceIdAndKeySourceComponentId(
        workspaceId: String,
        feedComponentId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
        pageable: Pageable,
    ): Slice<FeedMapping>

    suspend fun findAllByKeyWorkspaceIdAndKeySourceResourceIdAndKeySourceComponentId(
        workspaceId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
        pageable: Pageable,
    ): Slice<FeedMapping>

}
