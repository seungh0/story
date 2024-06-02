package com.story.core.domain.workspace

interface WorkspaceArchiveWriteRepository {

    suspend fun delete(workspaceArchive: WorkspaceArchive)

}
