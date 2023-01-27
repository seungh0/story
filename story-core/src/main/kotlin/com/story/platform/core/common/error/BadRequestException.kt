package com.story.platform.core.common.error

data class BadRequestException(
    override val message: String,
    override val errorCode: com.story.platform.core.common.error.ErrorCode = com.story.platform.core.common.error.ErrorCode.E400_BAD_REQUEST,
    override val cause: Throwable? = null,
) : com.story.platform.core.common.error.StoryBaseException(message = message, errorCode = errorCode, cause = cause) {

    constructor(message: String, cause: Throwable?) : this(
        message = message,
        errorCode = com.story.platform.core.common.error.ErrorCode.E400_BAD_REQUEST,
        cause = cause
    )

}
