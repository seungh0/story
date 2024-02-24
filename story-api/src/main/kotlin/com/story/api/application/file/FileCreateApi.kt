package com.story.api.application.file

import com.story.api.config.apikey.ApiKeyContext
import com.story.api.config.apikey.RequestApiKey
import com.story.core.common.model.dto.ApiResponse
import com.story.core.domain.file.FileCreator
import com.story.core.domain.file.FileResponse
import com.story.core.domain.file.FileType
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class FileCreateApi(
    private val fileCreator: FileCreator,
) {

    @PostMapping("/v1/files/{fileType}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    suspend fun createFile(
        @PathVariable fileType: FileType,
        @RequestApiKey authContext: ApiKeyContext,
        @RequestPart files: List<MultipartFile>,
    ): ApiResponse<List<FileResponse>> {
        val response = fileCreator.create(
            workspaceId = authContext.workspaceId,
            fileType = fileType,
            multipartFiles = files,
        )
        return ApiResponse.ok(response)
    }

}
