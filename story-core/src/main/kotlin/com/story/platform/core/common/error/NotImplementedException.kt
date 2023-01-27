package com.story.platform.core.common.error

data class NotImplementedException(
    override val message: String,
    override val errorCode: ErrorCode = ErrorCode.E501_NOT_IMPLEMENTED,
    override val cause: Throwable? = null,
) : StoryBaseException(message = message, errorCode = errorCode, cause = cause) {

    constructor(message: String, cause: Throwable?) : this(
        message = message,
        errorCode = ErrorCode.E501_NOT_IMPLEMENTED,
        cause = cause
    )

}
