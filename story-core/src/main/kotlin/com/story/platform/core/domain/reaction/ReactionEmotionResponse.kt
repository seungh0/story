package com.story.platform.core.domain.reaction

data class ReactionEmotionResponse(
    val emotionId: String,
    val count: Long,
    val reactedByMe: Boolean,
)
