package com.story.core.domain.post

data class PostPatchResponse(
    val post: PostWithSections,
    val hasChanged: Boolean,
)
