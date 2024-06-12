package com.story.core.domain.file

import com.story.core.support.file.FileUploader
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class FileCreator(
    private val fileUploader: FileUploader,
    private val fileWriteRepository: FileWriteRepository,
) {

    suspend fun create(
        workspaceId: String,
        fileType: FileType,
        multipartFiles: Collection<MultipartFile>,
    ): List<File> {
        val fileInfos = fileUploader.upload(fileType = fileType, files = multipartFiles)
        return fileWriteRepository.saveAll(workspaceId = workspaceId, fileType = fileType, fileInfos = fileInfos)
    }

}
