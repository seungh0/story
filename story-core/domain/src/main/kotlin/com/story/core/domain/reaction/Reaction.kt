package com.story.core.domain.reaction

data class Reaction(
    val workspaceId: String,
    val componentId: String,
    val spaceId: String,
    val emotionIds: Set<String>,
)
