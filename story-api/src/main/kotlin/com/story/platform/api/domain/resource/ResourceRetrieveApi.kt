package com.story.platform.api.domain.resource

import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.domain.resource.ResourceId
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ResourceRetrieveApi {

    /**
     * 워크스페이스에서 사용할 수 있는 리소스 목록을 조회합니다
     */
    @GetMapping("/v1/resources")
    suspend fun listResources(): ApiResponse<List<ResourceApiResponse>> {
        val resourceTypes = ResourceId.values().map { resourceId ->
            ResourceApiResponse(
                resourceId = resourceId.code,
                description = resourceId.description,
            )
        }
        return ApiResponse.ok(resourceTypes)
    }

}
