package com.story.api.application.post

import com.story.core.domain.post.PostId

data class PostCreateApiResponse(
    val postId: PostId,
) {

    companion object {
        fun of(
            postId: PostId,
        ) = PostCreateApiResponse(
            postId = postId,
        )
    }

}
