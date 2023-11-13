package com.story.platform.core.domain.post

import com.story.platform.core.common.model.dto.AuditingTimeResponse
import com.story.platform.core.domain.post.section.PostSection
import com.story.platform.core.domain.post.section.PostSectionContentResponse
import com.story.platform.core.domain.post.section.PostSectionType
import com.story.platform.core.domain.post.section.image.ImagePostSectionContentResponse
import com.story.platform.core.domain.post.section.text.TextPostSectionContentResponse

data class PostResponse(
    val workspaceId: String,
    val componentId: String,
    val spaceId: String,
    val postId: Long,
    val accountId: String,
    val title: String,
    val sections: List<PostSectionContentResponse>,
    val extra: Map<String, String>,
) : AuditingTimeResponse() {

    companion object {
        fun of(post: Post, sections: List<PostSection>): PostResponse {
            val response = PostResponse(
                workspaceId = post.key.workspaceId,
                componentId = post.key.componentId,
                spaceId = post.key.spaceId,
                postId = post.key.postId,
                accountId = post.accountId,
                title = post.title,
                sections = sections.map { section ->
                    when (section.sectionType) {
                        PostSectionType.IMAGE -> ImagePostSectionContentResponse.fromContent(content = section.data)
                        PostSectionType.TEXT -> TextPostSectionContentResponse.fromContent(content = section.data)
                    }
                },
                extra = post.extra,
            )
            response.from(post.auditingTime)
            return response
        }
    }

}
