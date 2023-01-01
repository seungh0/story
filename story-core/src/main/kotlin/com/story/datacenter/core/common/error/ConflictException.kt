package com.story.datacenter.core.common.error

data class ConflictException(
    override val message: String,
    override val errorCode: ErrorCode = ErrorCode.E409_CONFLICT,
) : StoryBaseException(message, errorCode)