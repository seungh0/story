package com.story.api.application.workspace

import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.workspace.WorkspaceNotExistsException
import com.story.core.domain.workspace.WorkspaceReaderWithCache
import com.story.core.domain.workspace.WorkspaceStatus
import kotlin.jvm.optionals.getOrNull

@HandlerAdapter
class WorkspaceRetrieveHandler(
    private val workspaceReaderWithCache: WorkspaceReaderWithCache,
) {

    suspend fun validateEnabledWorkspace(
        workspaceId: String,
    ) {
        val workspace = workspaceReaderWithCache.getWorkspace(workspaceId = workspaceId)
            .orElseThrow { WorkspaceNotExistsException(message = "워크스페이스($workspaceId)가 존재하지 않습니다") }

        if (!workspace.isEnabled()) {
            throw WorkspaceNotExistsException(message = "워크스페이스($workspaceId)가 활성화되지 않았습니다")
        }
    }

    suspend fun getWorkspace(
        workspaceId: String,
        filterStatus: WorkspaceStatus? = WorkspaceStatus.ENABLED,
    ): WorkspaceResponse {
        val workspace = workspaceReaderWithCache.getWorkspace(workspaceId = workspaceId).getOrNull()

        if (workspace == null || filterStatus != null && filterStatus != workspace.status) {
            throw WorkspaceNotExistsException(message = "워크스페이스($workspaceId)가 존재하지 않습니다 [filterStaus: $filterStatus, current: ${workspace?.status}]")
        }
        return WorkspaceResponse.of(workspace = workspace)
    }

}
