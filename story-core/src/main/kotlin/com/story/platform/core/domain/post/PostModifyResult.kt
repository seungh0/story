package com.story.platform.core.domain.post

data class PostModifyResult(
    val post: Post,
    val hasChanged: Boolean,
)
