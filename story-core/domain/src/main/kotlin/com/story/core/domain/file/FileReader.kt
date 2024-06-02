package com.story.core.domain.file

import org.springframework.stereotype.Service

@Service
class FileReader(
    private val fileReadRepository: FileReadRepository,
) {

    suspend fun getFiles(
        workspaceId: String,
        fileType: FileType,
        fileIds: Collection<Long>,
    ): Map<Long, File> {
        return fileReadRepository.getFiles(
            workspaceId = workspaceId,
            fileType = fileType,
            fileIds = fileIds,
        )
    }

}
