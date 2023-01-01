package com.story.datacenter.core.common.model

import com.story.datacenter.core.common.enums.CursorDirection
import com.story.datacenter.core.common.error.BadRequestException
import javax.validation.constraints.Max
import javax.validation.constraints.Min

data class CursorRequest(
    val cursor: String? = null,

    val direction: CursorDirection = CursorDirection.NEXT,

    @field:Min(value = 1)
    @field:Max(value = 30)
    val pageSize: Int = 0,
) {

    init {
        if (cursor == null && direction != CursorDirection.NEXT) {
            throw BadRequestException("첫 페이지 조회시에는 NEXT 방향으로만 조회할 수 있습니다")
        }
    }

}

