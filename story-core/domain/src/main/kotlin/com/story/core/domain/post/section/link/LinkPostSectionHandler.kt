package com.story.core.domain.post.section.link

import com.story.core.domain.post.section.PostSectionContent
import com.story.core.domain.post.section.PostSectionContentCommand
import com.story.core.domain.post.section.PostSectionContentEntity
import com.story.core.domain.post.section.PostSectionHandler
import com.story.core.domain.post.section.PostSectionType
import org.springframework.stereotype.Service

@Service
class LinkPostSectionHandler : PostSectionHandler {

    override fun sectionType(): PostSectionType = PostSectionType.LINK

    override suspend fun makeContents(
        workspaceId: String,
        requests: Collection<PostSectionContentCommand>,
    ): Map<PostSectionContentCommand, PostSectionContentEntity> {
        return requests.map { request ->
            if (request !is LinkPostSectionContentCommand) {
                throw IllegalArgumentException("request is not LinkPostSectionContentCommand")
            }
            return@map request to LinkPostSectionContentEntity(
                link = request.link,
                extra = request.extra,
            )
        }.toMap()
    }

    override suspend fun makeContentResponse(contents: Collection<PostSectionContentEntity>): Map<PostSectionContentEntity, PostSectionContent> {
        return contents.associateWith { content ->
            (content as LinkPostSectionContentEntity).toSectionContent()
        }
    }

}
