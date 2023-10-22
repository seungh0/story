package com.story.platform.core.domain.post

import com.story.platform.core.common.error.ErrorCode
import com.story.platform.core.common.error.StoryBaseException

data class PostIdInvalidException(
    override val message: String,
    override val cause: Throwable? = null,
) : StoryBaseException(
    message = message,
    errorCode = ErrorCode.E400_INVALID_ARGUMENTS,
    cause = cause,
    reasons = listOf("postId is invalid"),
)
