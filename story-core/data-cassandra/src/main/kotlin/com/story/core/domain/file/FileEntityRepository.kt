package com.story.core.domain.file

import com.story.core.support.file.FileInfo
import com.story.core.support.file.FileProperties
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Repository

@Repository
class FileEntityRepository(
    private val fileCassandraRepository: FileCassandraRepository,
    private val properties: FileProperties, // TODO: 레이어 바꿔야함
) : FileWriteRepository, FileReadRepository {

    override suspend fun getFiles(workspaceId: String, fileType: FileType, fileIds: Collection<Long>): Map<Long, File> {
        val files = fileIds.groupBy { fileId -> FileIdHelper.getSlot(fileId) }
            .map { (slotId, fileIdsInSlot) ->
                fileCassandraRepository.findAllByKeyWorkspaceIdAndKeyFileTypeAndKeySlotIdAndKeyFileIdIn(
                    workspaceId = workspaceId,
                    fileType = fileType,
                    slotId = slotId,
                    fileIds = fileIdsInSlot,
                )
            }.flatten()
        return files.asSequence()
            .map { file -> file.toFile(properties.getProperties(fileType = fileType).domain) }
            .associateBy { file -> file.fileId }
    }

    override suspend fun saveAll(workspaceId: String, fileType: FileType, fileInfos: Collection<FileInfo>): List<File> {
        val files = fileInfos.map { fileInfo ->
            FileEntity.of(
                workspaceId = workspaceId,
                fileType = fileType,
                fileInfo = fileInfo,
            )
        }
        fileCassandraRepository.saveAll(files).toList()
        return files.map { file -> file.toFile(domain = properties.getProperties(fileType).domain) }
    }

}
