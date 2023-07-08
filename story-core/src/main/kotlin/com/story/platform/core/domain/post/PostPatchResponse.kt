package com.story.platform.core.domain.post

data class PostPatchResponse(
    val post: Post,
    val hasChanged: Boolean,
)
