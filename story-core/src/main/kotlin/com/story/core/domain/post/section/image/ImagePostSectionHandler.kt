package com.story.core.domain.post.section.image

import com.story.core.domain.post.section.PostSectionContent
import com.story.core.domain.post.section.PostSectionContentRequest
import com.story.core.domain.post.section.PostSectionContentResponse
import com.story.core.domain.post.section.PostSectionHandler
import com.story.core.domain.post.section.PostSectionType
import com.story.core.infrastructure.photo.PhotoProperties
import org.springframework.stereotype.Service

@Service
class ImagePostSectionHandler(
    private val properties: PhotoProperties,
) : PostSectionHandler {

    override fun sectionType(): PostSectionType = PostSectionType.IMAGE

    override fun makeContents(requests: Collection<PostSectionContentRequest>): Map<PostSectionContentRequest, PostSectionContent> {
        return requests.associateWith { request ->
            if (request !is ImagePostSectionContentRequest) {
                throw IllegalArgumentException("request is not ImagePostSectionContentRequest")
            }
            return@associateWith ImagePostSectionContent(
                path = request.path,
                width = 0,
                height = 0,
                fileSize = 0, // TODO: 계산해서 넣기
                fileName = request.fileName,
            )
        }
    }

    override fun makeContentResponse(contents: Collection<PostSectionContent>): Map<PostSectionContent, PostSectionContentResponse> {
        return contents.associateWith { content ->
            ImagePostSectionContentResponse.from(
                sectionContent = content as ImagePostSectionContent,
                imageDomain = properties.domain
            )
        }
    }

}
