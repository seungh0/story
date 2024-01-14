package com.story.core.domain.event

import com.story.core.common.error.ErrorCode
import com.story.core.common.error.StoryBaseException

data class EventKeyInvalidException(
    override val message: String,
    override val cause: Throwable? = null,
) : StoryBaseException(
    message = message,
    errorCode = ErrorCode.E400_INVALID_EVENT_KEY,
    cause = cause,
)
