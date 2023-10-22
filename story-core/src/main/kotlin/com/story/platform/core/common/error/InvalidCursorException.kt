package com.story.platform.core.common.error

data class InvalidCursorException(
    override val message: String,
    override val cause: Throwable? = null,
) : StoryBaseException(
    message = message,
    errorCode = ErrorCode.E400_INVALID_ARGUMENTS,
    cause = cause,
    reasons = listOf("cursor is invalid"),
)
