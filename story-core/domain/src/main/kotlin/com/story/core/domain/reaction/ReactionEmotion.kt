package com.story.core.domain.reaction

data class ReactionEmotion(
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
        ) = ReactionEmotion(
            emotionId = emotionId,
            count = MIN_COUNT.coerceAtLeast(count ?: MIN_COUNT),
            reactedByMe = reactedByMe,
        )

        fun zero(
            emotionId: String,
        ) = ReactionEmotion(
            emotionId = emotionId,
            count = MIN_COUNT,
            reactedByMe = false,
        )
    }

}
