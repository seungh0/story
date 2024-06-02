package com.story.api.application.resource

import com.story.core.domain.resource.ResourceId

data class ResourceResponse(
    val resourceId: String,
    val description: String,
    val latestVersion: String,
) {

    companion object {
        fun from(resourceId: ResourceId) = ResourceResponse(
            resourceId = resourceId.code,
            description = resourceId.description,
            latestVersion = resourceId.latestVersion.version,
        )
    }

}
