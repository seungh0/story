package com.story.platform.core.common.error

data class NotSupportedException(
    override val message: String,
    override val errorCode: ErrorCode = ErrorCode.E501_NOT_SUPPORTED,
    override val cause: Throwable? = null,
) : StoryBaseException(message = message, errorCode = errorCode, cause = cause)
