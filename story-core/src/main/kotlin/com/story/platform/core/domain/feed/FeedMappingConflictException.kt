package com.story.platform.core.domain.feed

import com.story.platform.core.common.error.ErrorCode
import com.story.platform.core.common.error.StoryBaseException

data class FeedMappingConflictException(
    override val message: String,
    override val cause: Throwable? = null,
) : StoryBaseException(
    message = message,
    errorCode = ErrorCode.E409_ALREADY_CONNECT_FEED_MAPPING,
    cause = cause,
)
