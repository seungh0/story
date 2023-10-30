package com.story.platform.api.domain.workspace

import com.story.platform.core.common.annotation.HandlerAdapter
import com.story.platform.core.domain.workspace.WorkspaceNotExistsException
import com.story.platform.core.domain.workspace.WorkspaceRetriever
import com.story.platform.core.domain.workspace.WorkspaceStatus

@HandlerAdapter
class WorkspaceRetrieveHandler(
    private val workspaceRetriever: WorkspaceRetriever,
) {

    suspend fun validateEnabledWorkspace(
        workspaceId: String,
    ) {
        val workspace = workspaceRetriever.getWorkspace(workspaceId = workspaceId)
        if (!workspace.isEnabled()) {
            throw WorkspaceNotExistsException(message = "워크스페이스($workspaceId)가 존재하지 않습니다")
        }
    }

    suspend fun getWorkspace(
        workspaceId: String,
        filterStatus: WorkspaceStatus? = WorkspaceStatus.ENABLED,
    ): WorkspaceApiResponse {
        val workspace = workspaceRetriever.getWorkspace(workspaceId = workspaceId)
        if (filterStatus != null && filterStatus == workspace.status) {
            throw WorkspaceNotExistsException(message = "워크스페이스($workspaceId)가 존재하지 않습니다 [filterStaus: $filterStatus, current: ${workspace.status}]")
        }
        return WorkspaceApiResponse.of(workspace = workspace)
    }

}
