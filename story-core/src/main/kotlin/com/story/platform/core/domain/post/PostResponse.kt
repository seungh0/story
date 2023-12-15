package com.story.platform.core.domain.post

import com.story.platform.core.common.model.dto.AuditingTimeResponse
import com.story.platform.core.domain.post.section.PostSection
import com.story.platform.core.domain.post.section.PostSectionContentResponse

data class PostResponse(
    val workspaceId: String,
    val componentId: String,
    val spaceId: String,
    val postId: Long,
    val accountId: String,
    val title: String,
    val sections: List<PostSectionContentResponse>,
    val extra: Map<String, String>,
    val metadata: PostMetadataResponse,
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
                sections = sections.map { section -> section.sectionType.toTypedResponse(sectionData = section.data) },
                extra = post.extra,
                metadata = PostMetadataResponse(
                    hasChildren = post.getMetadata(metadata = PostMetadata.HAS_CHILDREN),
                ),
            )
            response.setAuditingTime(post.auditingTime)
            return response
        }
    }

}

data class PostMetadataResponse(
    val hasChildren: Boolean,
)
