package com.story.platform.api.domain.resource

import com.story.platform.core.common.spring.HandlerAdapter
import com.story.platform.core.domain.resource.ResourceId

@HandlerAdapter
class ResourceRetrieveHandler {

    suspend fun listResources(): List<ResourceApiResponse> {
        return ResourceId.values().map { resourceId ->
            ResourceApiResponse(
                resourceId = resourceId.code,
                description = resourceId.description,
            )
        }
    }

}
