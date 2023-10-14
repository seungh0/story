package com.story.platform.core.common.error

data class MissingRequestAccountIdException(
    override val message: String,
    override val cause: Throwable? = null,
) : StoryBaseException(
    message = message,
    errorCode = ErrorCode.E400_MISSING_REQUEST_ACCOUNT_ID,
    cause = cause,
)
