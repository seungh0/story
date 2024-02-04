package com.story.api.application.component

import com.story.api.config.apikey.ApiKeyContext
import com.story.api.config.apikey.RequestApiKey
import com.story.core.common.model.dto.ApiResponse
import com.story.core.domain.resource.ResourceId
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ComponentCreateApi(
    private val componentCreateHandler: ComponentCreateHandler,
) {

    /**
     * 신규 컴포넌트를 생성합니다
     */
    @PostMapping("/v1/resources/{resourceId}/components/{componentId}")
    suspend fun createComponent(
        @PathVariable resourceId: String,
        @PathVariable componentId: String,
        @RequestApiKey authContext: ApiKeyContext,
        @Valid @RequestBody request: ComponentCreateApiRequest,
    ): ApiResponse<Nothing?> {
        componentCreateHandler.createComponent(
            workspaceId = authContext.workspaceId,
            resourceId = ResourceId.findByCode(resourceId),
            componentId = componentId,
            description = request.description,
        )
        return ApiResponse.OK
    }

}
