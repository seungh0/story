package com.story.platform.api.domain.component

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.common.model.CursorRequest
import com.story.platform.core.common.model.CursorResult
import com.story.platform.core.domain.component.ComponentResponse
import com.story.platform.core.domain.component.ComponentRetriever
import com.story.platform.core.domain.component.ResourceId
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class ComponentRetrieveApi(
    private val componentRetriever: ComponentRetriever,
) {

    /**
     * 워크스페이스에 등록된 특정 리소스의 컴포넌트 목록을 조회합니다
     */
    @GetMapping("/v1/resources/{resourceId}/components")
    suspend fun listComponents(
        @PathVariable resourceId: String,
        @RequestAuthContext authContext: AuthContext,
        @Valid cursorRequest: CursorRequest,
    ): ApiResponse<CursorResult<ComponentResponse, String>> {
        val response = componentRetriever.listComponents(
            workspaceId = authContext.workspaceId,
            resourceId = ResourceId.findByCode(resourceId),
            cursorRequest = cursorRequest,
        )
        return ApiResponse.success(response)
    }

}
