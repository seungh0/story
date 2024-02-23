package com.story.api.application.post

import com.fasterxml.jackson.annotation.JsonIgnore
import com.story.core.common.model.CursorDirection
import com.story.core.common.model.dto.CursorRequestConvertible
import com.story.core.domain.post.PostKey
import com.story.core.domain.post.PostSortBy
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class PostListApiRequest(
    val parentId: PostKey?,

    val sortBy: String = PostSortBy.LATEST.name,
    override val cursor: String? = null,

    override val direction: String = CursorDirection.NEXT.name,

    @field:Min(value = 1)
    @field:Max(value = 50)
    override val pageSize: Int = 0,
) : CursorRequestConvertible {

    @JsonIgnore
    fun getSortBy(): PostSortBy {
        return PostSortBy.findByCode(sortBy)
    }

}
