package com.story.core.domain.component

import com.story.core.domain.resource.ResourceId
import org.springframework.data.domain.Pageable

interface ComponentReadRepository {

    suspend fun existsById(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
    ): Boolean

    suspend fun findById(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
    ): Component?

    suspend fun findAllByKeyWorkspaceIdAndKeyResourceId(
        workspaceId: String,
        resourceId: ResourceId,
        pageable: Pageable,
    ): List<Component>

    suspend fun findAllByKeyWorkspaceIdAndKeyResourceIdAndKeyComponentIdLessThan(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        pageable: Pageable,
    ): List<Component>

}
