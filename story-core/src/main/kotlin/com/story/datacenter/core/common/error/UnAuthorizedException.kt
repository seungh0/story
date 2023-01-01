package com.story.datacenter.core.common.error

data class UnAuthorizedException(
    override val message: String,
    override val errorCode: ErrorCode = ErrorCode.E401_UNAUTHORIZED,
) : StoryBaseException(message, errorCode)