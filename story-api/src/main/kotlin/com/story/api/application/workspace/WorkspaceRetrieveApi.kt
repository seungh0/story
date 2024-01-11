package com.story.api.application.workspace

import com.story.api.config.auth.AuthContext
import com.story.api.config.auth.RequestAuthContext
import com.story.core.common.model.dto.ApiResponse
import com.story.core.domain.workspace.WorkspaceNoPermissionException
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class WorkspaceRetrieveApi(
    private val workspaceRetrieveHandler: WorkspaceRetrieveHandler,
) {

    @GetMapping("/v1/workspaces/{workspaceId}")
    suspend fun getWorkspace(
        @PathVariable workspaceId: String,
        @RequestAuthContext authContext: AuthContext,
        @Valid request: WorkspaceGetApiRequest,
    ): ApiResponse<WorkspaceApiResponse> {
        if (authContext.workspaceId != workspaceId) {
            throw WorkspaceNoPermissionException("워크스페이스($workspaceId)에 권한이 없습니다 [현재 요청자의 워크스페이스 키 = ${authContext.workspaceId}]")
        }

        val response = workspaceRetrieveHandler.getWorkspace(
            workspaceId = workspaceId,
            filterStatus = request.filterStatus,
        )
        return ApiResponse.ok(response)
    }

}
