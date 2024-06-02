package com.story.core.domain.component

import com.story.core.common.error.ErrorCode
import com.story.core.common.error.StoryBaseException

data class ComponentAlreadyExistsException(
    override val message: String,
    override val cause: Throwable? = null,
) : StoryBaseException(
    message = message,
    errorCode = ErrorCode.E409_ALREADY_EXISTS_COMPONENT,
    cause = cause,
)
