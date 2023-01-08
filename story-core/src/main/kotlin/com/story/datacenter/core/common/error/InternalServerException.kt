package com.story.datacenter.core.common.error

data class InternalServerException(
    override val message: String,
    override val errorCode: ErrorCode = ErrorCode.E500_INTERNAL_SERVER_ERROR,
    override val cause: Throwable? = null,
) : StoryBaseException(message = message, errorCode = errorCode, cause = cause) {

    constructor(message: String, cause: Throwable?) : this(
        message = message,
        errorCode = ErrorCode.E500_INTERNAL_SERVER_ERROR,
        cause = cause
    )

}
