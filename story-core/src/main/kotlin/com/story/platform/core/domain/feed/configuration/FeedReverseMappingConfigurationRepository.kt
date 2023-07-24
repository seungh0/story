package com.story.platform.core.domain.feed.configuration

import com.story.platform.core.domain.event.EventAction
import com.story.platform.core.domain.resource.ResourceId
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FeedReverseMappingConfigurationRepository :
    CoroutineCrudRepository<FeedReverseMappingConfiguration, FeedReverseMappingConfigurationPrimaryKey> {

    suspend fun findAllByKeyWorkspaceIdAndKeySourceResourceIdAndKeySourceComponentIdAndKeyEventActionAndKeyTargetResourceId(
        workspaceId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
        eventAction: EventAction,
        targetResourceId: ResourceId,
        pageable: Pageable,
    ): Slice<FeedReverseMappingConfiguration>

}
