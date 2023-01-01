package com.story.datacenter.core.common.error

data class NotImplementedException(
    override val message: String,
    override val errorCode: ErrorCode = ErrorCode.E501_NOT_IMPLEMENTED,
) : StoryBaseException(message, errorCode)
