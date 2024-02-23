package com.story.api.application.component

import com.story.core.common.model.CursorDirection
import com.story.core.common.model.dto.CursorRequestConvertible
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class ComponentListApiRequest(
    @field:Min(value = 1)
    @field:Max(value = 50)
    override val pageSize: Int = 0,
) : CursorRequestConvertible {

    override val direction: String = CursorDirection.NEXT.name
    override val cursor: String? = null

}
