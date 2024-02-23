package com.story.core.common.model.dto

import com.story.core.common.model.CursorDirection
import java.util.Base64

interface CursorRequestConvertible {

    val pageSize: Int
    val direction: String
    val cursor: String?

    fun toDecodedCursor(): CursorRequest {
        return CursorRequest(
            cursor = this.cursor,
            direction = CursorDirection.findByCode(code = direction),
            pageSize = pageSize,
        )
    }

    fun toSimpleCursor(): CursorRequest {
        return CursorRequest(
            cursor = this.cursor?.let { String(Base64.getUrlDecoder().decode(cursor)) },
            direction = CursorDirection.findByCode(code = direction),
            pageSize = pageSize,
        )
    }

}
