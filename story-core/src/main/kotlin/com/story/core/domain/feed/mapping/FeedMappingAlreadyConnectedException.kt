package com.story.core.domain.feed.mapping

import com.story.core.common.error.ErrorCode
import com.story.core.common.error.StoryBaseException

data class FeedMappingAlreadyConnectedException(
    override val message: String,
    override val cause: Throwable? = null,
) : StoryBaseException(
    message = message,
    errorCode = ErrorCode.E409_ALREADY_CONNECTED_FEED_MAPPING,
    cause = cause,
)
