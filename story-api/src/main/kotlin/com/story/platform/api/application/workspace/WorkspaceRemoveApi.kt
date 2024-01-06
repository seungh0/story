package com.story.platform.api.application.workspace

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.core.common.model.dto.ApiResponse
import com.story.platform.core.domain.workspace.WorkspaceNoPermissionException
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class WorkspaceRemoveApi(
    private val workspaceRemoveHandler: WorkspaceRemoveHandler,
) {

    @DeleteMapping("/v1/workspaces/{workspaceId}")
    suspend fun removeWorkspace(
        @PathVariable workspaceId: String,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<Nothing?> {
        if (authContext.workspaceId != workspaceId) {
            throw WorkspaceNoPermissionException("워크스페이스($workspaceId)에 권한이 없습니다 [현재 요청자의 워크스페이스 키 = ${authContext.workspaceId}]")
        }

        workspaceRemoveHandler.removeWorkspace(
            workspaceId = workspaceId,
        )
        return ApiResponse.OK
    }

}
