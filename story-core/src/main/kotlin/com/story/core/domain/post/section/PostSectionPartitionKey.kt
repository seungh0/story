package com.story.core.domain.post.section

import com.story.core.domain.post.Post
import com.story.core.domain.post.PostReverse

data class PostSectionPartitionKey(
    val workspaceId: String,
    val componentId: String,
    val spaceId: String,
    val parentId: String,
    val slotId: Long,
) {

    companion object {
        fun from(post: Post) = PostSectionPartitionKey(
            workspaceId = post.key.workspaceId,
            componentId = post.key.componentId,
            spaceId = post.key.spaceId,
            parentId = post.key.parentId,
            slotId = PostSectionSlotAssigner.assign(post.key.postId),
        )

        fun from(post: PostReverse) = PostSectionPartitionKey(
            workspaceId = post.key.workspaceId,
            componentId = post.key.componentId,
            spaceId = post.key.spaceId,
            parentId = post.key.parentId,
            slotId = PostSectionSlotAssigner.assign(post.key.postId),
        )
    }

}
