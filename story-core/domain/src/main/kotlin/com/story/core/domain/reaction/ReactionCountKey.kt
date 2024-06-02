package com.story.core.domain.reaction

data class ReactionCountKey(
    val workspaceId: String,
    val componentId: String,
    val spaceId: String,
    val emotionId: String,
)
