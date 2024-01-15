package com.story.core.domain.feed.mapping

import com.story.core.domain.resource.ResourceId
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface FeedMappingReverseRepository :
    CoroutineCrudRepository<FeedMappingReverse, FeedMappingReversePrimaryKey> {

    fun findAllByKeyWorkspaceIdAndKeySourceResourceIdAndKeySourceComponentId(
        workspaceId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
        pageable: Pageable,
    ): Flow<FeedMappingReverse>

}
