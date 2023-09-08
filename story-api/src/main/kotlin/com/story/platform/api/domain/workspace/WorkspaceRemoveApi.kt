package com.story.platform.api.domain.workspace

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.core.common.model.dto.ApiResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class WorkspaceRemoveApi(
    private val workspaceRemoveHandler: WorkspaceRemoveHandler,
) {

    @DeleteMapping("/workspaces")
    suspend fun remove(
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<Nothing?> {
        workspaceRemoveHandler.remove(
            workspaceId = authContext.workspaceId,
        )
        return ApiResponse.OK
    }

}
