package com.story.core.domain.post

import com.story.core.common.model.dto.AuditingTimeResponse
import com.story.core.domain.post.section.PostSectionContent

data class PostWithSections(
    val workspaceId: String,
    val componentId: String,
    val spaceId: String,
    val parentId: PostId?,
    val postId: PostId,
    val depth: Int,
    val ownerId: String,
    val title: String,
    val sections: List<PostSectionContent>,
    val extra: Map<String, String>,
    val metadata: PostMetadataResponse?,
) : AuditingTimeResponse() {

    fun hasChildrenMetadata(): Boolean {
        if (this.metadata == null) {
            return false
        }
        return this.metadata.hasChildren
    }

    companion object {
        fun of(post: PostEntity, sections: List<PostSectionContent>): PostWithSections {
            val response = PostWithSections(
                workspaceId = post.key.workspaceId,
                componentId = post.key.componentId,
                spaceId = post.key.spaceId,
                parentId = post.key.parentPostId,
                postId = post.key.postId,
                depth = post.key.getDepth(),
                ownerId = post.ownerId,
                title = post.title,
                sections = sections,
                extra = post.extra,
                metadata = PostMetadataResponse.of(post),
            )
            response.setAuditingTime(post.auditingTime)
            return response
        }

        fun of(post: PostReverse, sections: List<PostSectionContent>): PostWithSections {
            val response = PostWithSections(
                workspaceId = post.key.workspaceId,
                componentId = post.key.componentId,
                spaceId = post.key.spaceId,
                parentId = post.key.parentPostId,
                postId = post.key.postId,
                depth = post.key.getDepth(),
                ownerId = post.key.ownerId,
                title = post.title,
                sections = sections,
                extra = post.extra,
                metadata = null
            )
            response.setAuditingTime(post.auditingTime)
            return response
        }
    }

}