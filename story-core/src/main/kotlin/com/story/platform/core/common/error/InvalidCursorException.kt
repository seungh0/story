package com.story.platform.core.common.error

data class InvalidCursorException(
    override val message: String,
    override val cause: Throwable? = null,
) : StoryBaseException(
    message = message,
    errorCode = ErrorCode.E400_INVALID_CURSOR,
    cause = cause,
)