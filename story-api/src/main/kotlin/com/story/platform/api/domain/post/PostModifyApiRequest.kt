package com.story.platform.api.domain.post

import com.story.platform.core.common.error.InvalidArgumentsException
import jakarta.validation.constraints.Size

data class PostModifyApiRequest(
    @field:Size(max = 100)
    val title: String?,

    @field:Size(max = 500)
    val content: String?,
) {

    init {
        if (title != null && title.isBlank()) {
            throw InvalidArgumentsException("title($title)가 빈 값일 수 없습니다")
        }
    }

}
