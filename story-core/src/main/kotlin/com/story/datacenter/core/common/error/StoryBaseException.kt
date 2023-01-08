package com.story.datacenter.core.common.error

abstract class StoryBaseException(
    override val message: String,
    open val errorCode: ErrorCode,
    override val cause: Throwable? = null,
) : RuntimeException(message, cause)
