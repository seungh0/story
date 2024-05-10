package com.story.core.domain.post

data class PostPatchResponse(
    val post: Post,
    val hasChanged: Boolean,
)
