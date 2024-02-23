package com.story.api.application.post

import com.story.core.common.model.Slice
import com.story.core.common.model.dto.CursorResponse
import com.story.core.common.model.dto.encode
import com.story.core.domain.post.PostResponse

data class PostListApiResponse(
    val posts: List<PostApiResponse>,
    val cursor: CursorResponse<String>,
) {

    companion object {
        fun of(posts: Slice<PostResponse, String>, requestUserId: String?) = PostListApiResponse(
            posts = posts.data.map { post -> PostApiResponse.of(post = post, requestUserId = requestUserId) },
            cursor = posts.cursor.encode(),
        )
    }

}
