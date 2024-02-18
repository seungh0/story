package com.story.api.application.post

import com.fasterxml.jackson.annotation.JsonIgnore
import com.story.core.common.model.CursorDirection
import com.story.core.common.model.dto.CursorRequest
import com.story.core.domain.post.PostKey
import com.story.core.domain.post.PostSortBy
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class PostListApiRequest(
    val parentId: PostKey?,

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
