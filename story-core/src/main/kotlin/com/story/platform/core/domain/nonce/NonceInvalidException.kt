package com.story.platform.core.domain.nonce

import com.story.platform.core.common.error.ErrorCode
import com.story.platform.core.common.error.StoryBaseException

data class NonceInvalidException(
    override val message: String,
) : StoryBaseException(
    message = message,
    errorCode = ErrorCode.E400_INVALID_NONCE,
)
