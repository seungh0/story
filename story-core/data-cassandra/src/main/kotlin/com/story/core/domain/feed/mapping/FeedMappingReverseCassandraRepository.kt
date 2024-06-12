package com.story.core.domain.feed.mapping

import com.story.core.domain.resource.ResourceId
import com.story.core.support.cassandra.CassandraBasicRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface FeedMappingReverseCassandraRepository :
    CassandraBasicRepository<FeedMappingReverseEntity, FeedMappingReversePrimaryKey> {

    suspend fun findAllByKeyWorkspaceIdAndKeySourceResourceIdAndKeySourceComponentId(
        workspaceId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
        pageable: Pageable,
    ): Slice<FeedMappingReverseEntity>

}
