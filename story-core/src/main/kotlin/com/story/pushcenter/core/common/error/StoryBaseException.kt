package com.story.pushcenter.core.common.error

abstract class StoryBaseException(
    override val message: String,
    open val errorCode: ErrorCode,
) : RuntimeException(message)
