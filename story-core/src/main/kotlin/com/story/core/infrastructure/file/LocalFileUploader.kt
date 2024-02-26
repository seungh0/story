package com.story.core.infrastructure.file

import com.story.core.domain.file.FileType
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Service
class LocalFileUploader(
    private val properties: FileProperties,
) : FileUploader {

    override suspend fun upload(fileType: FileType, files: Collection<MultipartFile>): List<FileInfo> {
        return files.map {
            val fileName = UUID.randomUUID().toString()
            FileInfo(
                domain = properties.getProperties(fileType).domain,
                width = 80,
                height = 60,
                path = "/image/$fileName",
                fileSize = 1024,
            )
        }
    }

}
