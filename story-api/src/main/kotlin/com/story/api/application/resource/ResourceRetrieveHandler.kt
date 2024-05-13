package com.story.api.application.resource

import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.resource.ResourceId

@HandlerAdapter
class ResourceRetrieveHandler {

    suspend fun listResources(request: ResourceListApiRequest): ResourceListApiResponse {
        val resources = ResourceId.entries.map { resourceId ->
            ResourceApiResponse.from(resourceId)
        }.take(request.pageSize)

        return ResourceListApiResponse(
            resources = resources,
        )
    }

}
