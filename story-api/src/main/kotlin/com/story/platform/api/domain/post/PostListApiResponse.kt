package com.story.platform.api.domain.post

import com.story.platform.core.common.model.ContentsWithCursor
import com.story.platform.core.common.model.Cursor
import com.story.platform.core.domain.post.PostResponse

data class PostListApiResponse(
    val posts: List<PostApiResponse>,
    val cursor: Cursor<String>,
) {

    companion object {
        fun of(posts: ContentsWithCursor<PostResponse, String>) = PostListApiResponse(
            posts = posts.data.map { post -> PostApiResponse.of(post = post) },
            cursor = posts.cursor,
        )
    }

}
