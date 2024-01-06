package com.story.platform.api.application.resource

import com.story.platform.core.common.annotation.HandlerAdapter
import com.story.platform.core.domain.resource.ResourceId

@HandlerAdapter
class ResourceRetrieveHandler {

    suspend fun listResources(request: ResourceListApiRequest): ResourceListApiResponse {
        val resources = ResourceId.values().map { resourceId ->
            ResourceApiResponse(
                resourceId = resourceId.code,
                description = resourceId.description,
            )
        }.take(request.pageSize)

        return ResourceListApiResponse(
            resources = resources,
        )
    }

}
