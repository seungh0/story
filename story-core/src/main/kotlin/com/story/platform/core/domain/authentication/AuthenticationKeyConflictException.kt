package com.story.platform.core.domain.authentication

import com.story.platform.core.common.error.ErrorCode
import com.story.platform.core.common.error.StoryBaseException

data class AuthenticationKeyConflictException(
    override val message: String,
    override val cause: Throwable? = null,
) : StoryBaseException(
    message = message,
    errorCode = ErrorCode.E409_ALREADY_EXISTS_AUTHENTICATION_KEY,
    cause = cause,
)
