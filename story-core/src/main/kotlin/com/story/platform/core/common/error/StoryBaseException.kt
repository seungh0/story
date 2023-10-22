package com.story.platform.core.common.error

abstract class StoryBaseException(
    override val message: String,
    open val errorCode: ErrorCode,
    override val cause: Throwable? = null,
    open val reasons: List<String>? = null,
) : RuntimeException(message, cause)
