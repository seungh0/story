package com.story.core.domain.workspace

interface WorkspaceReadRepository {

    suspend fun findById(workspaceId: String): Workspace?

}
