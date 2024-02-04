package com.story.api.application.workspace

import com.story.api.config.apikey.ApiKeyContext
import com.story.api.config.apikey.RequestApiKey
import com.story.core.common.model.dto.ApiResponse
import com.story.core.domain.workspace.WorkspaceNoPermissionException
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
        @RequestApiKey authContext: ApiKeyContext,
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
