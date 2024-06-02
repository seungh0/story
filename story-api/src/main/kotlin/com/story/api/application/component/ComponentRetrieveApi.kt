package com.story.api.application.component

import com.story.api.config.apikey.ApiKeyContext
import com.story.api.config.apikey.RequestApiKey
import com.story.core.common.model.dto.ApiResponse
import com.story.core.domain.resource.ResourceId
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class ComponentRetrieveApi(
    private val componentRetrieveHandler: ComponentRetrieveHandler,
) {

    @GetMapping("/v1/resources/{resourceId}/components/{componentId}")
    suspend fun getComponent(
        @PathVariable resourceId: String,
        @PathVariable componentId: String,
        @RequestApiKey authContext: ApiKeyContext,
        @Valid request: ComponentGetRequest,
    ): ApiResponse<ComponentResponse> {
        val component = componentRetrieveHandler.getComponent(
            workspaceId = authContext.workspaceId,
            resourceId = ResourceId.findByCode(resourceId),
            componentId = componentId,
            request = request,
        )
        return ApiResponse.ok(component)
    }

    @GetMapping("/v1/resources/{resourceId}/components")
    suspend fun listComponents(
        @PathVariable resourceId: String,
        @RequestApiKey authContext: ApiKeyContext,
        @Valid request: ComponentListRequest,
    ): ApiResponse<ComponentListResponse> {
        val response = componentRetrieveHandler.listComponents(
            workspaceId = authContext.workspaceId,
            resourceId = ResourceId.findByCode(resourceId),
            request = request,
        )
        return ApiResponse.ok(response)
    }

}
