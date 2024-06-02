package com.story.core.domain.post.section.image

import com.story.core.common.utils.mapToSet
import com.story.core.domain.file.FileNotExistsException
import com.story.core.domain.file.FileReader
import com.story.core.domain.file.FileType
import com.story.core.domain.post.section.PostSectionContent
import com.story.core.domain.post.section.PostSectionContentCommand
import com.story.core.domain.post.section.PostSectionContentEntity
import com.story.core.domain.post.section.PostSectionHandler
import com.story.core.domain.post.section.PostSectionType
import com.story.core.infrastructure.file.FileProperties
import org.springframework.stereotype.Service

@Service
class ImagePostSectionHandler(
    private val properties: FileProperties,
    private val fileReader: FileReader,
) : PostSectionHandler {

    override fun sectionType(): PostSectionType = PostSectionType.IMAGE

    override suspend fun makeContents(
        workspaceId: String,
        requests: Collection<PostSectionContentCommand>,
    ): Map<PostSectionContentCommand, PostSectionContentEntity> {
        val fileIds = requests.mapToSet { request ->
            if (request !is ImagePostSectionContentCommand) {
                throw IllegalArgumentException("request is not ImagePostSectionContentCommand")
            }
            return@mapToSet request.fileId
        }

        val files = fileReader.getFiles(workspaceId = workspaceId, fileType = FileType.IMAGE, fileIds = fileIds)

        return requests.associateWith { request ->
            if (request !is ImagePostSectionContentCommand) {
                throw IllegalArgumentException("request is not ImagePostSectionContentCommand")
            }

            val file = files[request.fileId]
                ?: throw FileNotExistsException("워크스페이스($workspaceId)에 등록된 이미지(IMAGE) 파일(${request.fileId})이 존재하지 않습니다")

            return@associateWith ImagePostSectionContentEntity(
                path = file.path,
                width = file.width,
                height = file.height,
                fileSize = file.fileSize,
                extra = request.extra,
            )
        }
    }

    override suspend fun makeContentResponse(contents: Collection<PostSectionContentEntity>): Map<PostSectionContentEntity, PostSectionContent> {
        return contents.associateWith { content ->
            ImagePostSectionContent.from(
                sectionContent = content as ImagePostSectionContentEntity,
                imageDomain = properties.getProperties(fileType = FileType.IMAGE).domain
            )
        }
    }

}
