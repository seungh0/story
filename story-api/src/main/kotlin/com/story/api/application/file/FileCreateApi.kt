package com.story.api.application.file

import com.story.core.common.model.dto.ApiResponse
import com.story.core.domain.file.FileCreator
import com.story.core.domain.file.FileResponse
import com.story.core.domain.file.FileType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class FileCreateApi(
    private val fileCreator: FileCreator,
) {

    @PostMapping("/v1/files")
    suspend fun createFile(
        workspaceId: String,
        fileType: FileType,
        files: Collection<MultipartFile>,
    ): ApiResponse<List<FileResponse>> {
        val response = fileCreator.create(
            workspaceId = workspaceId,
            fileType = fileType,
            multipartFiles = files,
        )
        return ApiResponse.ok(response)
    }

}
