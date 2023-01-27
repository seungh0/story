package com.story.platform.core.common.error

data class ForbiddenException(
    override val message: String,
    override val errorCode: ErrorCode = ErrorCode.E403_FORBIDDEN,
    override val cause: Throwable? = null,
) : StoryBaseException(message = message, errorCode = errorCode, cause = cause) {

    constructor(message: String, cause: Throwable?) : this(
        message = message,
        errorCode = ErrorCode.E403_FORBIDDEN,
        cause = cause
    )

}

