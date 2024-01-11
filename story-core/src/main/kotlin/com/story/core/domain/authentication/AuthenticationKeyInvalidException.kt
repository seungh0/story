package com.story.core.domain.authentication

import com.story.core.common.error.ErrorCode
import com.story.core.common.error.StoryBaseException

data class AuthenticationKeyInvalidException(
    override val message: String,
    override val errorCode: ErrorCode = ErrorCode.E401_INVALID_AUTHENTICATION_KEY,
    override val cause: Throwable? = null,
) : StoryBaseException(message = message, errorCode = errorCode, cause = cause)
