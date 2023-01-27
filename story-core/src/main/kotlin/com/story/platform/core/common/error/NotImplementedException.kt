package com.story.platform.core.common.error

data class NotImplementedException(
    override val message: String,
    override val errorCode: com.story.platform.core.common.error.ErrorCode = com.story.platform.core.common.error.ErrorCode.E501_NOT_IMPLEMENTED,
    override val cause: Throwable? = null,
) : com.story.platform.core.common.error.StoryBaseException(message = message, errorCode = errorCode, cause = cause) {

    constructor(message: String, cause: Throwable?) : this(
        message = message,
        errorCode = com.story.platform.core.common.error.ErrorCode.E501_NOT_IMPLEMENTED,
        cause = cause
    )

}
