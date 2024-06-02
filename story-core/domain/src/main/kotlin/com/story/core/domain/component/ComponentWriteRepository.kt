package com.story.core.domain.component

import com.story.core.domain.resource.ResourceId

interface ComponentWriteRepository {

    suspend fun create(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        description: String,
        status: ComponentStatus,
    ): Component

    suspend fun update(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        description: String?,
        status: ComponentStatus?,
    ): Component

}
