package com.story.platform.core.domain.post

data class PostSpaceKey(
    val workspaceId: String,
    val spaceType: PostSpaceType,
    val spaceId: String,
)
