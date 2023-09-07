package com.story.platform.core.domain.reaction

data class ReactionResponse(
    val workspaceId: String,
    val componentId: String,
    val targetId: String,
    val emotions: List<ReactionEmotionResponse>,
)
