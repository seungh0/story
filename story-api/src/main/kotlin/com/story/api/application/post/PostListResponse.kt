package com.story.api.application.post

import com.story.core.common.model.Slice
import com.story.core.common.model.dto.CursorResponse
import com.story.core.common.model.dto.encode
import com.story.core.domain.post.Post

data class PostListResponse(
    val posts: List<PostResponse>,
    val cursor: CursorResponse<String>,
) {

    companion object {
        fun of(posts: Slice<Post, String>, requestUserId: String?) = PostListResponse(
            posts = posts.data.map { post -> PostResponse.of(post = post, requestUserId = requestUserId) },
            cursor = posts.cursor.encode(),
        )
    }

}
