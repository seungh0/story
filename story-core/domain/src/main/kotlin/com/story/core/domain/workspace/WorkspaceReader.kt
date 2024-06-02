package com.story.core.domain.workspace

import org.springframework.stereotype.Service

@Service
class WorkspaceReader(
    private val workspaceReadRepository: WorkspaceReadRepository,
) {

    suspend fun getWorkspace(
        workspaceId: String,
    ): Workspace? {
        return workspaceReadRepository.findById(workspaceId)
    }

}
