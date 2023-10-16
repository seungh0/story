package com.story.platform.core.domain.reaction

data class ReactionEmotionResponse(
    val emotionId: String,
    val count: Long,
    val reactedByMe: Boolean,
) {

    companion object {
        private const val MIN_COUNT = 0L

        fun of(
            emotionId: String,
            count: Long?,
            reactedByMe: Boolean,
        ) = ReactionEmotionResponse(
            emotionId = emotionId,
            count = MIN_COUNT.coerceAtLeast(count ?: MIN_COUNT),
            reactedByMe = reactedByMe,
        )

        fun zero(
            emotionId: String,
        ) = ReactionEmotionResponse(
            emotionId = emotionId,
            count = MIN_COUNT,
            reactedByMe = false,
        )
    }

}
