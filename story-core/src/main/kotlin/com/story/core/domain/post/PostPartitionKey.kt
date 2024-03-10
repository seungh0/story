package com.story.core.domain.post

data class PostPartitionKey(
    val workspaceId: String,
    val componentId: String,
    val spaceId: String,
    val parentId: String,
    val slotId: Long,
) {

    companion object {
        fun from(postReverse: PostReverse) = PostPartitionKey(
            workspaceId = postReverse.key.workspaceId,
            componentId = postReverse.key.componentId,
            spaceId = postReverse.key.spaceId,
            parentId = postReverse.key.parentId,
            slotId = postReverse.slotId,
        )
    }

}
