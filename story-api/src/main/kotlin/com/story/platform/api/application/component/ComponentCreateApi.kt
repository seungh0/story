package com.story.platform.api.application.component

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.core.common.model.dto.ApiResponse
import com.story.platform.core.domain.resource.ResourceId
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
        @RequestAuthContext authContext: AuthContext,
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
