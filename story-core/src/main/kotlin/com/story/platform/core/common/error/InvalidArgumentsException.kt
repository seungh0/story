package com.story.platform.core.common.error

data class InvalidArgumentsException(
    override val message: String,
    override val errorCode: ErrorCode = ErrorCode.E400_INVALID_ARGUMENTS,
    override val cause: Throwable? = null,
) : StoryBaseException(message = message, errorCode = errorCode, cause = cause)
