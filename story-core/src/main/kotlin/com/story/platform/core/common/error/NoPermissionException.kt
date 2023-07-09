package com.story.platform.core.common.error

data class NoPermissionException(
    override val message: String,
    override val cause: Throwable? = null,
) : StoryBaseException(
    message = message,
    errorCode = ErrorCode.E403_NO_PERMISSION,
    cause = cause,
)
