package com.story.core.domain.feed.mapping

import com.story.core.domain.resource.ResourceId
import com.story.core.infrastructure.cassandra.CassandraBasicRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable

interface FeedMappingReverseRepository :
    CassandraBasicRepository<FeedMappingReverse, FeedMappingReversePrimaryKey> {

    fun findAllByKeyWorkspaceIdAndKeySourceResourceIdAndKeySourceComponentId(
        workspaceId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
        pageable: Pageable,
    ): Flow<FeedMappingReverse>

}
