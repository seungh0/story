package com.story.api.application.resource

import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.resource.ResourceId

@HandlerAdapter
class ResourceRetrieveHandler {

    suspend fun listResources(request: ResourceListRequest): ResourceListResponse {
        val resources = ResourceId.entries.map { resourceId ->
            ResourceResponse.from(resourceId)
        }.take(request.pageSize)

        return ResourceListResponse(
            resources = resources,
        )
    }

}
