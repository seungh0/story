package com.story.api.application.post

import com.story.core.domain.post.PostId

data class PostCreateResponse(
    val postId: PostId,
) {

    companion object {
        fun of(
            postId: PostId,
        ) = PostCreateResponse(
            postId = postId,
        )
    }

}
