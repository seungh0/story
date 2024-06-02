package com.story.core.domain.feed.mapping

import com.story.core.common.error.ErrorCode
import com.story.core.common.error.StoryBaseException

data class FeedMappingCapacityExceededException(
    override val message: String,
    override val cause: Throwable? = null,
) : StoryBaseException(
    message = message,
    errorCode = ErrorCode.E403_FEED_MAPPING_CAPACITY_EXCEEDED,
    cause = cause,
)
