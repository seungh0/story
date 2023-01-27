package com.story.platform.core.common.error

data class BadRequestException(
    override val message: String,
    override val errorCode: ErrorCode = ErrorCode.E400_BAD_REQUEST,
    override val cause: Throwable? = null,
) : StoryBaseException(message = message, errorCode = errorCode, cause = cause) {

    constructor(message: String, cause: Throwable?) : this(
        message = message,
        errorCode = ErrorCode.E400_BAD_REQUEST,
        cause = cause
    )

}
