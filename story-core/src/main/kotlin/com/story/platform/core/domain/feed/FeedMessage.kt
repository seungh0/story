package com.story.platform.core.domain.feed

import com.story.platform.core.domain.post.Post

data class FeedMessage(
    val title: String,
    val content: String,
    val imageUrl: String?,
) {

    companion object {
        fun of(post: Post) = FeedMessage(
            title = post.title,
            content = post.content,
            imageUrl = null,
        )
    }

}
