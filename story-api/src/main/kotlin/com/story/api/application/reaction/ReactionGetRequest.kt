package com.story.api.application.reaction

data class ReactionGetRequest(
    val includeUnselectedEmotions: Boolean = false,
)
