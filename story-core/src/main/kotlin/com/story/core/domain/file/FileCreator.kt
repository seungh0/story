package com.story.core.domain.file

import com.story.core.infrastructure.file.FileProperties
import com.story.core.infrastructure.file.FileUploader
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class FileCreator(
    private val fileRepository: FileRepository,
    private val fileUploader: FileUploader,
    private val properties: FileProperties,
) {

    suspend fun create(
        workspaceId: String,
        fileType: FileType,
        multipartFiles: Collection<MultipartFile>,
    ): List<File> {
        val fileInfos = fileUploader.upload(fileType = fileType, files = multipartFiles)
        val files = fileInfos.map { fileInfo ->
            FileEntity.of(
                workspaceId = workspaceId,
                fileType = fileType,
                fileInfo = fileInfo,
            )
        }
        fileRepository.saveAll(files).toList()
        return files.map { file -> File.of(file = file, domain = properties.getProperties(fileType).domain) }
    }

}
