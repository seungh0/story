package com.story.platform.core.domain.feed.mapping

import com.story.platform.core.domain.resource.ResourceId
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FeedReverseMappingConfigurationRepository :
    CoroutineCrudRepository<FeedReverseMappingConfiguration, FeedReverseMappingConfigurationPrimaryKey> {

    fun findAllByKeyWorkspaceIdAndKeySourceResourceIdAndKeySourceComponentId(
        workspaceId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
        pageable: Pageable,
    ): Flow<FeedReverseMappingConfiguration>

}
