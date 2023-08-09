package com.story.platform.api.domain.workspace

import com.story.platform.core.common.spring.HandlerAdapter
import com.story.platform.core.domain.workspace.WorkspaceNotExistsException
import com.story.platform.core.domain.workspace.WorkspaceRetriever

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

}
