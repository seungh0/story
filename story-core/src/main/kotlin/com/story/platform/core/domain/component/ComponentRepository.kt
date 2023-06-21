package com.story.platform.core.domain.component

import com.story.platform.core.domain.resource.ResourceId
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ComponentRepository : CoroutineCrudRepository<Component, ComponentPrimaryKey> {

    suspend fun findAllByKeyWorkspaceIdAndKeyResourceIdAndKeyComponentIdLessThan(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        pageable: Pageable,
    ): Slice<Component>

    suspend fun findAllByKeyWorkspaceIdAndKeyResourceId(
        workspaceId: String,
        resourceId: ResourceId,
        pageable: Pageable,
    ): Slice<Component>

}
