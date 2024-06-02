package com.story.core.domain.file

interface FileReadRepository {

    suspend fun getFiles(
        workspaceId: String,
        fileType: FileType,
        fileIds: Collection<Long>,
    ): Map<Long, File>

}
