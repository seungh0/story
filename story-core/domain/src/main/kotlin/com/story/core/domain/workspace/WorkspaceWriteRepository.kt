package com.story.core.domain.workspace

interface WorkspaceWriteRepository {

    suspend fun delete(workspaceId: String)

}
