package com.story.core.domain.feed.mapping

import com.story.core.domain.resource.ResourceId
import com.story.core.infrastructure.cassandra.CassandraBasicRepository
import org.springframework.data.domain.Pageable

interface FeedMappingRepository :
    CassandraBasicRepository<FeedMappingEntity, FeedMappingPrimaryKey> {

    suspend fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySourceResourceIdAndKeySourceComponentId(
        workspaceId: String,
        feedComponentId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
        pageable: Pageable,
    ): List<FeedMappingEntity>

}
