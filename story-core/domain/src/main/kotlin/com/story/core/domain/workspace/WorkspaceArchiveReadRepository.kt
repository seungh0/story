package com.story.core.domain.workspace

interface WorkspaceArchiveReadRepository {

    suspend fun findById(workspaceId: String): WorkspaceArchive?

}
