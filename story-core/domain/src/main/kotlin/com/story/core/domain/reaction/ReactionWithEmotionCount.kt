package com.story.core.domain.reaction

data class ReactionWithEmotionCount(
    val workspaceId: String,
    val componentId: String,
    val spaceId: String,
    val emotions: List<ReactionEmotion>,
)
