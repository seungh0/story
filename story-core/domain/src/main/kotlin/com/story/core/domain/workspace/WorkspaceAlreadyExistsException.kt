package com.story.core.domain.workspace

import com.story.core.common.error.ErrorCode
import com.story.core.common.error.StoryBaseException

data class WorkspaceAlreadyExistsException(
    override val message: String,
    override val cause: Throwable? = null,
) : StoryBaseException(
    message = message,
    errorCode = ErrorCode.E409_ALREADY_EXISTS_WORKSPACE,
    cause = cause,
)
