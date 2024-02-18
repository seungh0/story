package com.story.core.domain.post

import com.story.core.common.model.dto.AuditingTimeResponse
import com.story.core.domain.post.section.PostSection
import com.story.core.domain.post.section.PostSectionContentResponse

data class PostResponse(
    val workspaceId: String,
    val componentId: String,
    val spaceId: String,
    val parentId: PostKey?,
    val postId: PostKey,
    val depth: Int,
    val ownerId: String,
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
                parentId = post.key.parentIdKey,
                postId = PostKey(
                    spaceId = post.key.spaceId,
                    parentId = post.key.parentId,
                    depth = post.key.toPostKey().depth,
                    postId = post.key.postId,
                ),
                depth = post.key.getDepth(),
                ownerId = post.ownerId,
                title = post.title,
                sections = sections.map { section -> section.sectionType.toTypedResponse(sectionData = section.data) },
                extra = post.extra,
                metadata = PostMetadataResponse(
                    hasChildren = post.getMetadata(type = PostMetadataType.HAS_CHILDREN),
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
