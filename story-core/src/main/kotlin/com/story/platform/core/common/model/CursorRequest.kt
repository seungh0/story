package com.story.platform.core.common.model

import com.story.platform.core.common.enums.CursorDirection
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class CursorRequest(
    val cursor: String? = null,

    val direction: CursorDirection = CursorDirection.NEXT,

    @field:Min(value = 1)
    @field:Max(value = 30)
    val pageSize: Int = 0,
)
