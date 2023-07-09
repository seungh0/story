package com.story.platform.core.domain.authentication

import com.story.platform.core.common.error.ErrorCode
import com.story.platform.core.common.error.StoryBaseException

data class AuthenticationKeyInvalidException(
    override val message: String,
    override val errorCode: ErrorCode = ErrorCode.E401_INVALID_AUTHENTICATION_KEY,
    override val cause: Throwable? = null,
) : StoryBaseException(message = message, errorCode = errorCode, cause = cause)
