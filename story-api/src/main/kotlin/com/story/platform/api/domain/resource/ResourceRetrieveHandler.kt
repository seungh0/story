package com.story.platform.api.domain.resource

import com.story.platform.core.common.model.Cursor
import com.story.platform.core.common.model.CursorResult
import com.story.platform.core.common.model.dto.CursorRequest
import com.story.platform.core.common.spring.HandlerAdapter
import com.story.platform.core.domain.resource.ResourceId

@HandlerAdapter
class ResourceRetrieveHandler {

    suspend fun listResources(cursorRequest: CursorRequest): CursorResult<ResourceApiResponse, String> {
        val resources = ResourceId.values().map { resourceId ->
            ResourceApiResponse(
                resourceId = resourceId.code,
                description = resourceId.description,
            )
        }.take(cursorRequest.pageSize)
        return CursorResult.of(data = resources, cursor = Cursor.noMore())
    }

}
