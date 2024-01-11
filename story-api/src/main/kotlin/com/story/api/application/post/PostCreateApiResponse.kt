package com.story.api.application.post

data class PostCreateApiResponse(
    val postId: String,
) {

    companion object {
        fun of(
            postId: Long,
        ) = PostCreateApiResponse(
            postId = postId.toString(),
        )
    }

}
