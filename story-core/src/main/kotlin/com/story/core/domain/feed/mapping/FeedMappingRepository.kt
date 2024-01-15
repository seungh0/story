package com.story.core.domain.feed.mapping

import com.story.core.domain.resource.ResourceId
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface FeedMappingRepository :
    CoroutineCrudRepository<FeedMapping, FeedMappingPrimaryKey> {

    suspend fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySourceResourceIdAndKeySourceComponentId(
        workspaceId: String,
        feedComponentId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
        pageable: Pageable,
    ): List<FeedMapping>

}
