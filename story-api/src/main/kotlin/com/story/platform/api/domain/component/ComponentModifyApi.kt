package com.story.platform.api.domain.component

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.core.common.model.dto.ApiResponse
import com.story.platform.core.domain.resource.ResourceId
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ComponentModifyApi(
    private val componentModifyHandler: ComponentModifyHandler,
) {

    /**
     * 컴포넌트를 수정합니다
     */
    @PatchMapping("/v1/resources/{resourceId}/components/{componentId}")
    suspend fun patchComponent(
        @PathVariable resourceId: String,
        @PathVariable componentId: String,
        @RequestAuthContext authContext: AuthContext,
        @Valid @RequestBody request: ComponentModifyApiRequest,
    ): ApiResponse<Nothing?> {
        componentModifyHandler.patchComponent(
            workspaceId = authContext.workspaceId,
            resourceId = ResourceId.findByCode(resourceId),
            componentId = componentId,
            description = request.description,
            status = request.status,
        )
        return ApiResponse.OK
    }

}
