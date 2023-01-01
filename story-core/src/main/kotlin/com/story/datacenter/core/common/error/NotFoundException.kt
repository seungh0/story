package com.story.datacenter.core.common.error

data class NotFoundException(
    override val message: String,
    override val errorCode: ErrorCode = ErrorCode.E404_NOT_FOUND,
) : StoryBaseException(message, errorCode)
