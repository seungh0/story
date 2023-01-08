package com.story.datacenter.core.common.error

data class ConflictException(
    override val message: String,
    override val errorCode: ErrorCode = ErrorCode.E409_CONFLICT,
    override val cause: Throwable? = null,
) : StoryBaseException(message = message, errorCode = errorCode, cause = cause) {

    constructor(message: String, cause: Throwable?) : this(
        message = message,
        errorCode = ErrorCode.E409_CONFLICT,
        cause = cause
    )

}

