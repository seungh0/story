package com.story.core.domain.post

data class PostPatchResponse(
    val post: PostResponse,
    val hasChanged: Boolean,
)
