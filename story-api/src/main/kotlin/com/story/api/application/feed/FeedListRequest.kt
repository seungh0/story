package com.story.api.application.feed

import com.story.core.common.model.CursorDirection
import com.story.core.common.model.dto.CursorRequestConvertible
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class FeedListRequest(
    override val cursor: String? = null,

    override val direction: String = CursorDirection.NEXT.name,

    @field:Min(value = 1)
    @field:Max(value = 50)
    override val pageSize: Int = 0,
) : CursorRequestConvertible
