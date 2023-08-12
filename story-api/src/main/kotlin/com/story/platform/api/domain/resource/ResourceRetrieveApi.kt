package com.story.platform.api.domain.resource

import com.story.platform.core.common.model.CursorResult
import com.story.platform.core.common.model.dto.ApiResponse
import com.story.platform.core.common.model.dto.CursorRequest
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
        @Valid cursorRequest: CursorRequest,
    ): ApiResponse<CursorResult<ResourceApiResponse, String>> {
        val response = resourceRetrieveHandler.listResources(cursorRequest)
        return ApiResponse.ok(response)
    }

}
