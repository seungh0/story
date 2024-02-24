package com.story.core.domain.post.section.link

import com.story.core.domain.post.section.PostSectionContent
import com.story.core.domain.post.section.PostSectionContentRequest
import com.story.core.domain.post.section.PostSectionContentResponse
import com.story.core.domain.post.section.PostSectionHandler
import com.story.core.domain.post.section.PostSectionType
import org.springframework.stereotype.Service

@Service
class LinkPostSectionHandler : PostSectionHandler {

    override fun sectionType(): PostSectionType = PostSectionType.LINK

    override suspend fun makeContents(
        workspaceId: String,
        requests: Collection<PostSectionContentRequest>,
    ): Map<PostSectionContentRequest, PostSectionContent> {
        return requests.map { request ->
            if (request !is LinkPostSectionContentRequest) {
                throw IllegalArgumentException("request is not LinkPostSectionContentRequest")
            }
            return@map request to LinkPostSectionContent(
                link = request.link,
                extra = request.extra,
            )
        }.toMap()
    }

    override suspend fun makeContentResponse(contents: Collection<PostSectionContent>): Map<PostSectionContent, PostSectionContentResponse> {
        return contents.associateWith { content ->
            LinkPostSectionContentResponse.from(content as LinkPostSectionContent)
        }
    }

}
