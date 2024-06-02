package com.story.core.domain.apikey

import com.story.core.common.error.ErrorCode
import com.story.core.common.error.StoryBaseException

data class ApiKeyNotExistsException(
    override val message: String,
    override val cause: Throwable? = null,
) : StoryBaseException(
    message = message,
    errorCode = ErrorCode.E404_NOT_EXISTS_API_KEY,
    cause = cause,
)
