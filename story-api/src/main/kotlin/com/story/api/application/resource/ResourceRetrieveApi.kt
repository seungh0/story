package com.story.api.application.resource

import com.story.core.common.model.dto.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ResourceRetrieveApi(
    private val resourceRetrieveHandler: ResourceRetrieveHandler,
) {

    /**
     * 워크스페이스에서 사용할 수 있는 리소스 목록을 조회합니다
     */
    @GetMapping("/v1/resources")
    suspend fun listResources(
        @Valid request: ResourceListApiRequest,
    ): ApiResponse<ResourceListApiResponse> {
        val response = resourceRetrieveHandler.listResources(request)
        return ApiResponse.ok(response)
    }

}
