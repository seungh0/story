package com.story.pushcenter.core.common.error

data class BadRequestException(
    override val message: String,
    override val errorCode: ErrorCode = ErrorCode.E400_BAD_REQUEST,
) : StoryBaseException(message, errorCode)
