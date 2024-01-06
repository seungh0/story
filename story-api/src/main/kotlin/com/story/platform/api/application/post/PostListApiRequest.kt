package com.story.platform.api.application.post

import com.fasterxml.jackson.annotation.JsonIgnore
import com.story.platform.core.common.model.CursorDirection
import com.story.platform.core.common.model.dto.CursorRequest
import com.story.platform.core.domain.post.PostSortBy
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class PostListApiRequest(
    val sortBy: String = PostSortBy.LATEST.name,
    val cursor: String? = null,

    val direction: String = CursorDirection.NEXT.name,

    @field:Min(value = 1)
    @field:Max(value = 50)
    val pageSize: Int = 0,
) {

    @JsonIgnore
    fun getSortBy(): PostSortBy {
        return PostSortBy.findByCode(sortBy)
    }

    fun toCursor() = CursorRequest(
        cursor = cursor,
        direction = CursorDirection.findByCode(direction),
        pageSize = pageSize,
    )

}
