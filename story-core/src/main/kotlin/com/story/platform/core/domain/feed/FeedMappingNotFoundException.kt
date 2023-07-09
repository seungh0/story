package com.story.platform.core.domain.feed

import com.story.platform.core.common.error.ErrorCode
import com.story.platform.core.common.error.StoryBaseException

data class FeedMappingNotFoundException(
    override val message: String,
    override val cause: Throwable? = null,
) : StoryBaseException(
    message = message,
    errorCode = ErrorCode.E404_NOT_EXISTS_CONNECT_FEED_MAPPING,
    cause = cause,
)
