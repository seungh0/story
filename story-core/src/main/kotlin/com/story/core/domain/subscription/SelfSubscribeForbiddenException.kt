package com.story.core.domain.subscription

import com.story.core.common.error.ErrorCode
import com.story.core.common.error.StoryBaseException

data class SelfSubscribeForbiddenException(
    override val message: String,
    override val cause: Throwable? = null,
) : StoryBaseException(
    message = message,
    errorCode = ErrorCode.E403_SELF_SUBSCRIPTION_NOT_ALLOWED,
    cause = cause,
)
