package com.story.core.domain.authentication

import com.story.core.common.error.ErrorCode
import com.story.core.common.error.StoryBaseException

data class AuthenticationKeyNotExistsException(
    override val message: String,
    override val cause: Throwable? = null,
) : StoryBaseException(
    message = message,
    errorCode = ErrorCode.E404_NOT_EXISTS_AUTHENTICATION_KEY,
    cause = cause,
)
