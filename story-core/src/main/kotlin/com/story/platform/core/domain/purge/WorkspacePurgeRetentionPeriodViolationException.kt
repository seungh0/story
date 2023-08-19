package com.story.platform.core.domain.purge

import com.story.platform.core.common.error.ErrorCode
import com.story.platform.core.common.error.StoryBaseException

data class WorkspacePurgeRetentionPeriodViolationException(
    override val message: String,
    override val cause: Throwable? = null,
) : StoryBaseException(
    message = message,
    errorCode = ErrorCode.E404_NOT_EXISTS_WORKSPACE,
    cause = cause,
)
