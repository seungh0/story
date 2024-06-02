package com.story.core.domain.nonce

import com.story.core.common.error.ErrorCode
import com.story.core.common.error.StoryBaseException

data class NonceInvalidException(
    override val message: String,
) : StoryBaseException(
    message = message,
    errorCode = ErrorCode.E400_INVALID_NONCE,
)
