package com.story.platform.api.domain.post

import com.story.platform.core.domain.post.Post

data class PostResponse(
    val postId: String,
    val title: String,
    val content: String,
    val extraJson: String?,
) {

    companion object {
        fun of(post: Post) = PostResponse(
            postId = post.key.postId,
            title = post.title,
            content = post.content,
            extraJson = post.extraJson,
        )
    }

}
