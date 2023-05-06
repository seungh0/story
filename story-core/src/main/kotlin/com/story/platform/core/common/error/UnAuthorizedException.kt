package com.story.platform.core.common.error

data class UnAuthorizedException(
    override val message: String,
    override val errorCode: ErrorCode = ErrorCode.E401_UNAUTHORIZED,
    override val cause: Throwable? = null,
) : StoryBaseException(message = message, errorCode = errorCode, cause = cause) {

    constructor(message: String, cause: Throwable?) : this(
        message = message,
        errorCode = ErrorCode.E401_UNAUTHORIZED,
        cause = cause
    )

}
