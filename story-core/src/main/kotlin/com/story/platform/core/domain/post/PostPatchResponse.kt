package com.story.platform.core.domain.post

data class PostPatchResponse(
    val post: PostResponse,
    val hasChanged: Boolean,
)
