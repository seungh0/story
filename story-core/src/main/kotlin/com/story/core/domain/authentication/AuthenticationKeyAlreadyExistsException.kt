package com.story.core.domain.authentication

import com.story.core.common.error.ErrorCode
import com.story.core.common.error.StoryBaseException

data class AuthenticationKeyAlreadyExistsException(
    override val message: String,
    override val cause: Throwable? = null,
) : StoryBaseException(
    message = message,
    errorCode = ErrorCode.E409_ALREADY_EXISTS_AUTHENTICATION_KEY,
    cause = cause,
)
