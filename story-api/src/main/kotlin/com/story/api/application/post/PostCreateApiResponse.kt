package com.story.api.application.post

import com.story.core.domain.post.PostKey

data class PostCreateApiResponse(
    val postId: PostKey,
) {

    companion object {
        fun of(
            postId: PostKey,
        ) = PostCreateApiResponse(
            postId = postId,
        )
    }

}
