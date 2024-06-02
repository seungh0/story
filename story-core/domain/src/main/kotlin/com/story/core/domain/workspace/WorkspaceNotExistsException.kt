package com.story.core.domain.workspace

import com.story.core.common.error.ErrorCode
import com.story.core.common.error.StoryBaseException

data class WorkspaceNotExistsException(
    override val message: String,
    override val cause: Throwable? = null,
) : StoryBaseException(
    message = message,
    errorCode = ErrorCode.E404_NOT_EXISTS_WORKSPACE,
    cause = cause,
)
