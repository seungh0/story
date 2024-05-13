package com.story.api.application.resource

import com.story.core.domain.resource.ResourceId

data class ResourceApiResponse(
    val resourceId: String,
    val description: String,
    val latestVersion: String,
) {

    companion object {
        fun from(resourceId: ResourceId) = ResourceApiResponse(
            resourceId = resourceId.code,
            description = resourceId.description,
            latestVersion = resourceId.latestVersion.version,
        )
    }

}
