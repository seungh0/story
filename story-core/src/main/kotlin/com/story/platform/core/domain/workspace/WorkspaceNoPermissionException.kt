package com.story.platform.core.domain.workspace

import com.story.platform.core.common.error.ErrorCode
import com.story.platform.core.common.error.StoryBaseException

data class WorkspaceNoPermissionException(
    override val message: String,
    override val cause: Throwable? = null,
) : StoryBaseException(
    message = message,
    errorCode = ErrorCode.E403_WORKSPACE_NO_PERMISSION,
    cause = cause,
)
