package com.story.core.domain.post.section.text

import com.story.core.domain.post.section.PostSectionContent
import com.story.core.domain.post.section.PostSectionContentEntity
import com.story.core.domain.post.section.PostSectionContentRequest
import com.story.core.domain.post.section.PostSectionHandler
import com.story.core.domain.post.section.PostSectionType
import org.springframework.stereotype.Service

@Service
class TextPostSectionHandler : PostSectionHandler {

    override fun sectionType(): PostSectionType = PostSectionType.TEXT

    override suspend fun makeContents(
        workspaceId: String,
        requests: Collection<PostSectionContentRequest>,
    ): Map<PostSectionContentRequest, PostSectionContentEntity> {
        return requests.map { request ->
            if (request !is TextPostSectionContentRequest) {
                throw IllegalArgumentException("request is not TextPostSectionContentRequest")
            }
            return@map request to TextPostSectionContentEntity(
                content = request.content,
                extra = request.extra,
            )
        }.toMap()
    }

    override suspend fun makeContentResponse(contents: Collection<PostSectionContentEntity>): Map<PostSectionContentEntity, PostSectionContent> {
        return contents.associateWith { content ->
            TextPostSectionContent.from(content as TextPostSectionContentEntity)
        }
    }

}
