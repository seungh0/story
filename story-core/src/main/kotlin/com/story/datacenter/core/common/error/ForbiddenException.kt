package com.story.datacenter.core.common.error

data class ForbiddenException(
    override val message: String,
    override val errorCode: ErrorCode = ErrorCode.E403_FORBIDDEN,
) : StoryBaseException(message, errorCode)
