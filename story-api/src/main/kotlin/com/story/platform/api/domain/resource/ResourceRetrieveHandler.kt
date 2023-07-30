package com.story.platform.api.domain.resource

import com.story.platform.core.domain.resource.ResourceId
import org.springframework.stereotype.Service

@Service
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
