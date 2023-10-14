package com.story.platform.api.domain.subscription

import com.story.platform.core.common.model.CursorDirection
import com.story.platform.core.common.model.dto.CursorRequest
import com.story.platform.core.common.model.dto.CursorRequestConvertable
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class SubscriptionTargetListApiRequest(
    val cursor: String? = null,

    val direction: CursorDirection = CursorDirection.NEXT,

    @field:Min(value = 1)
    @field:Max(value = 50)
    val pageSize: Int = 0,
) : CursorRequestConvertable {

    override fun toCursor() = CursorRequest(
        cursor = cursor,
        direction = direction,
        pageSize = pageSize,
    )

}
