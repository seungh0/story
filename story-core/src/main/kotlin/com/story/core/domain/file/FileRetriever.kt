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
    ): Map<Long, FileResponse> {
        val fileIdsGroupBySlot = fileIds.groupBy { fileId -> FileIdHelper.getSlot(fileId) }

        val files = fileIdsGroupBySlot.map { (slotId, fileIdsInSlot) ->
            fileRepository.findAllByKeyWorkspaceIdAndKeyFileTypeAndKeySlotIdAndKeyFileIdIn(
                workspaceId = workspaceId,
                fileType = fileType,
                slotId = slotId,
                fileIds = fileIdsInSlot,
            )
        }.flatten()
        return files.asSequence()
            .map { file -> FileResponse.of(file = file, properties.getProperties(fileType = fileType).domain) }
            .associateBy { file -> file.fileId }
    }

}
