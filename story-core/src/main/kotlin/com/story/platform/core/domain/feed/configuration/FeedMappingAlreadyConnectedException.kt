package com.story.platform.core.domain.feed.configuration

import com.story.platform.core.common.error.ErrorCode
import com.story.platform.core.common.error.StoryBaseException

data class FeedMappingAlreadyConnectedException(
    override val message: String,
    override val cause: Throwable? = null,
) : StoryBaseException(
    message = message,
    errorCode = ErrorCode.E409_ALREADY_CONNECTED_FEED_MAPPING,
    cause = cause,
)
