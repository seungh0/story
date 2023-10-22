package com.story.platform.core.common.error

data class InvalidArgumentsException(
    override val message: String,
    override val cause: Throwable? = null,
    override val reasons: List<String>,
) : StoryBaseException(
    message = message,
    errorCode = ErrorCode.E400_INVALID_ARGUMENTS,
    cause = cause,
    reasons = reasons
)
