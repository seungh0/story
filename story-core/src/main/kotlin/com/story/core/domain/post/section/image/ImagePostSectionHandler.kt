package com.story.core.domain.post.section.image

import com.story.core.common.utils.mapToSet
import com.story.core.domain.file.FileNotExistsException
import com.story.core.domain.file.FileRetriever
import com.story.core.domain.file.FileType
import com.story.core.domain.post.section.PostSectionContent
import com.story.core.domain.post.section.PostSectionContentRequest
import com.story.core.domain.post.section.PostSectionContentResponse
import com.story.core.domain.post.section.PostSectionHandler
import com.story.core.domain.post.section.PostSectionType
import com.story.core.infrastructure.file.FileProperties
import org.springframework.stereotype.Service

@Service
class ImagePostSectionHandler(
    private val properties: FileProperties,
    private val fileRetriever: FileRetriever,
) : PostSectionHandler {

    override fun sectionType(): PostSectionType = PostSectionType.IMAGE

    override suspend fun makeContents(
        workspaceId: String,
        requests: Collection<PostSectionContentRequest>,
    ): Map<PostSectionContentRequest, PostSectionContent> {
        val fileIds = requests.mapToSet { request ->
            if (request !is ImagePostSectionContentRequest) {
                throw IllegalArgumentException("request is not ImagePostSectionContentRequest")
            }
            return@mapToSet request.fileId
        }

        val files = fileRetriever.getFiles(workspaceId = workspaceId, fileType = FileType.IMAGE, fileIds = fileIds)

        return requests.associateWith { request ->
            if (request !is ImagePostSectionContentRequest) {
                throw IllegalArgumentException("request is not ImagePostSectionContentRequest")
            }

            val file = files[request.fileId]
                ?: throw FileNotExistsException("워크스페이스($workspaceId)에 등록된 이미지(IMAGE) 파일(${request.fileId})이 존재하지 않습니다")

            return@associateWith ImagePostSectionContent(
                path = file.path,
                width = file.width,
                height = file.height,
                fileSize = file.fileSize,
                fileName = file.fileName,
                extra = request.extra,
            )
        }
    }

    override suspend fun makeContentResponse(contents: Collection<PostSectionContent>): Map<PostSectionContent, PostSectionContentResponse> {
        return contents.associateWith { content ->
            ImagePostSectionContentResponse.from(
                sectionContent = content as ImagePostSectionContent,
                imageDomain = properties.getProperties(fileType = FileType.IMAGE).domain
            )
        }
    }

}
