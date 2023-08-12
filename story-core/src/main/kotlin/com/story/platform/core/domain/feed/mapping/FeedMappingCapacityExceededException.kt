package com.story.platform.core.domain.feed.mapping

import com.story.platform.core.common.error.ErrorCode
import com.story.platform.core.common.error.StoryBaseException

data class FeedMappingCapacityExceededException(
    override val message: String,
    override val cause: Throwable? = null,
) : StoryBaseException(
    message = message,
    errorCode = ErrorCode.E403_FEED_MAPPING_CAPACITY_EXCEEDED,
    cause = cause,
)
