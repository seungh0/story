package com.story.core.domain.file

import com.story.core.infrastructure.file.FileProperties
import org.springframework.stereotype.Service

@Service
class FileRetriever(
    private val fileRepository: FileRepository,
    private val properties: FileProperties,
) {

    suspend fun getFiles(
        workspaceId: String,
        fileType: FileType,
        fileIds: Collection<Long>,
    ): Map<Long, File> {
        val files = fileIds.groupBy { fileId -> FileIdHelper.getSlot(fileId) }
            .map { (slotId, fileIdsInSlot) ->
                fileRepository.findAllByKeyWorkspaceIdAndKeyFileTypeAndKeySlotIdAndKeyFileIdIn(
                    workspaceId = workspaceId,
                    fileType = fileType,
                    slotId = slotId,
                    fileIds = fileIdsInSlot,
                )
            }.flatten()
        return files.asSequence()
            .map { file -> File.of(file = file, properties.getProperties(fileType = fileType).domain) }
            .associateBy { file -> file.fileId }
    }

}
