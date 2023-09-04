package com.story.platform.core.domain.reaction

data class ReactionOptionResponse(
    val optionId: String,
    val count: Long,
    val reactedByMe: Boolean,
)
