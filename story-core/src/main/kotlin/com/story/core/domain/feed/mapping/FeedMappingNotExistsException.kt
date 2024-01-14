package com.story.core.domain.feed.mapping

import com.story.core.common.error.ErrorCode
import com.story.core.common.error.StoryBaseException

data class FeedMappingNotExistsException(
    override val message: String,
    override val cause: Throwable? = null,
) : StoryBaseException(
    message = message,
    errorCode = ErrorCode.E404_NOT_EXISTS_FEED_MAPPING,
    cause = cause,
)
