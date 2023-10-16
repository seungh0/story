package com.story.platform.api.domain.emotion

import com.story.platform.core.common.error.InvalidArgumentsException

data class EmotionModifyApiRequest(
    val priority: Long?,
    val image: String?,
) {

    init {
        if (image != null && image.isBlank()) {
            throw InvalidArgumentsException("Emotion image($image)가 빈 값일 수 없습니다")
        }

        if (priority == null && image == null) {
            throw InvalidArgumentsException("Emotion을 변경하기 위한 최소한의 한개 필드가 존재해야 합니다")
        }
    }

}
