package com.story.core.domain.post.section.text

import com.story.core.domain.post.section.PostSectionContent
import com.story.core.domain.post.section.PostSectionContentRequest
import com.story.core.domain.post.section.PostSectionContentResponse
import com.story.core.domain.post.section.PostSectionHandler
import com.story.core.domain.post.section.PostSectionType
import org.springframework.stereotype.Service

@Service
class TextPostSectionHandler : PostSectionHandler {

    override fun sectionType(): PostSectionType = PostSectionType.TEXT

    override fun makeContents(requests: Collection<PostSectionContentRequest>): Map<PostSectionContentRequest, PostSectionContent> {
        return requests.map { request ->
            if (request !is TextPostSectionContentRequest) {
                throw IllegalArgumentException("request is not TextPostSectionContentRequest")
            }
            return@map request to TextPostSectionContent(
                content = request.content,
            )
        }.toMap()
    }

    override fun makeContentResponse(contents: Collection<PostSectionContent>): Map<PostSectionContent, PostSectionContentResponse> {
        return contents.associateWith { content ->
            TextPostSectionContentResponse.from(content as TextPostSectionContent)
        }
    }

}
