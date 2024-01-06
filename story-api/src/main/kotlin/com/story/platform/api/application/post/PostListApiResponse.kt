package com.story.platform.api.application.post

import com.story.platform.core.common.model.Slice
import com.story.platform.core.common.model.dto.CursorResponse
import com.story.platform.core.domain.post.PostResponse

data class PostListApiResponse(
    val posts: List<PostApiResponse>,
    val cursor: CursorResponse<String>,
) {

    companion object {
        fun of(posts: Slice<PostResponse, String>, requestAccountId: String?) = PostListApiResponse(
            posts = posts.data.map { post -> PostApiResponse.of(post = post, requestAccountId = requestAccountId) },
            cursor = posts.cursor,
        )
    }

}
