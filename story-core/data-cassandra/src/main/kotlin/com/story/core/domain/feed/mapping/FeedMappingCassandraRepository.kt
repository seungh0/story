package com.story.core.domain.feed.mapping

import com.story.core.domain.resource.ResourceId
import com.story.core.support.cassandra.CassandraBasicRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface FeedMappingCassandraRepository :
    CassandraBasicRepository<FeedMappingEntity, FeedMappingPrimaryKey> {

    suspend fun findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySourceResourceIdAndKeySourceComponentId(
        workspaceId: String,
        feedComponentId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
        pageable: Pageable,
    ): Slice<FeedMappingEntity>

}
