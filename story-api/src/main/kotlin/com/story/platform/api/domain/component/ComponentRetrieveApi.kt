package com.story.platform.api.domain.component

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.core.common.model.dto.ApiResponse
import com.story.platform.core.domain.resource.ResourceId
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
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<ComponentApiResponse> {
        val component = componentRetrieveHandler.getComponent(
            workspaceId = authContext.workspaceId,
            resourceId = ResourceId.findByCode(resourceId),
            componentId = componentId,
        )
        return ApiResponse.ok(component)
    }

    @GetMapping("/v1/resources/{resourceId}/components")
    suspend fun listComponents(
        @PathVariable resourceId: String,
        @RequestAuthContext authContext: AuthContext,
        @Valid request: ComponentListApiRequest,
    ): ApiResponse<ComponentListApiResponse> {
        val response = componentRetrieveHandler.listComponents(
            workspaceId = authContext.workspaceId,
            resourceId = ResourceId.findByCode(resourceId),
            request = request,
        )
        return ApiResponse.ok(response)
    }

}
