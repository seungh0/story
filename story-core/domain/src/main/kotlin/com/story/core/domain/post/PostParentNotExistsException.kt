package com.story.core.domain.post

import com.story.core.common.error.ErrorCode
import com.story.core.common.error.StoryBaseException

data class PostParentNotExistsException(
    override val message: String,
    override val cause: Throwable? = null,
) : StoryBaseException(
    message = message,
    errorCode = ErrorCode.E404_NOT_EXISTS_PARENT_POST,
    cause = cause,
)
