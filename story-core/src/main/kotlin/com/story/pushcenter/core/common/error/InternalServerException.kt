package com.story.pushcenter.core.common.error

data class InternalServerException(
    override val message: String,
    override val errorCode: ErrorCode = ErrorCode.E500_INTERNAL_SERVER_ERROR,
) : StoryBaseException(message, errorCode)
