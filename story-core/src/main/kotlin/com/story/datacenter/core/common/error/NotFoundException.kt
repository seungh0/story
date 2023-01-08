package com.story.datacenter.core.common.error

data class NotFoundException(
    override val message: String,
    override val errorCode: ErrorCode = ErrorCode.E404_NOT_FOUND,
    override val cause: Throwable? = null,
) : StoryBaseException(message = message, errorCode = errorCode, cause = cause) {

    constructor(message: String, cause: Throwable?) : this(
        message = message,
        errorCode = ErrorCode.E404_NOT_FOUND,
        cause = cause
    )

}

