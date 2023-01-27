package com.story.platform.core.common.model

import javax.validation.constraints.Max
import javax.validation.constraints.Min

data class CursorRequest(
    val cursor: String? = null,

    val direction: com.story.platform.core.common.enums.CursorDirection = com.story.platform.core.common.enums.CursorDirection.NEXT,

    @field:Min(value = 1)
    @field:Max(value = 30)
    val pageSize: Int = 0,
) {

    init {
        if (cursor == null && direction != com.story.platform.core.common.enums.CursorDirection.NEXT) {
            throw com.story.platform.core.common.error.BadRequestException("첫 페이지 조회시에는 NEXT 방향으로만 조회할 수 있습니다")
        }
    }

}

