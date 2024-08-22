package com.story.core.domain.workspace

interface WorkspaceWriteRepository {

    suspend fun create(workspaceId: String, name: String, plan: WorkspacePricePlan): Workspace

    suspend fun delete(workspaceId: String)

}
