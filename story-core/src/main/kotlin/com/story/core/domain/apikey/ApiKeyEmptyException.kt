package com.story.core.domain.apikey

import com.story.core.common.error.ErrorCode
import com.story.core.common.error.StoryBaseException

data class ApiKeyEmptyException(
    override val message: String,
    override val errorCode: ErrorCode = ErrorCode.E401_EMPTY_API_KEY,
    override val cause: Throwable? = null,
) : StoryBaseException(message = message, errorCode = errorCode, cause = cause)
