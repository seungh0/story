package com.story.platform.core.domain.emotion

import com.story.platform.core.common.error.ErrorCode
import com.story.platform.core.common.error.StoryBaseException

data class EmotionCountLimitExceedException(
    override val message: String,
    override val cause: Throwable? = null,
) : StoryBaseException(
    message = message,
    errorCode = ErrorCode.E403_EMOTION_COUNT_LIMIT_EXCEEDED,
    cause = cause,
)
