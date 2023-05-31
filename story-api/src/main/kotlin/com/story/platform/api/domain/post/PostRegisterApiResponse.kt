package com.story.platform.api.domain.post

data class PostRegisterApiResponse(
    val postId: String,
) {

    companion object {
        fun of(
            postId: Long,
        ) = PostRegisterApiResponse(
            postId = postId.toString(),
        )
    }

}
