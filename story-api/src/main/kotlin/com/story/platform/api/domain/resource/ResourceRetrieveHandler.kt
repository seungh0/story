package com.story.platform.api.domain.resource

import com.story.platform.core.common.annotation.HandlerAdapter
import com.story.platform.core.common.model.dto.CursorRequest
import com.story.platform.core.domain.resource.ResourceId

@HandlerAdapter
class ResourceRetrieveHandler {

    suspend fun listResources(cursorRequest: CursorRequest): ResourceListApiResponse {
        val resources = ResourceId.values().map { resourceId ->
            ResourceApiResponse(
                resourceId = resourceId.code,
                description = resourceId.description,
            )
        }.take(cursorRequest.pageSize)

        return ResourceListApiResponse(
            resources = resources,
        )
    }

}
