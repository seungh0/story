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
    val metadata: PostMetadata?,
) : AuditingTimeResponse() {

    fun hasChildrenMetadata(): Boolean {
        if (this.metadata == null) {
            return false
        }
        return this.metadata.hasChildren
    }

}
