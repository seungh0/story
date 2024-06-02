package com.story.core.common.error

data class MissingRequestUserIdException(
    override val message: String,
    override val cause: Throwable? = null,
) : StoryBaseException(
    message = message,
    errorCode = ErrorCode.E400_MISSING_REQUEST_USER_ID,
    cause = cause,
)
