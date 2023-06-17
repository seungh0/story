package com.story.platform.core.domain.feed

import com.story.platform.core.domain.post.PostResponse

data class FeedMessage(
    val title: String,
    val content: String,
    val imageUrl: String?,
) {

    companion object {
        fun of(post: PostResponse) = FeedMessage(
            title = post.title,
            content = post.content,
            imageUrl = null,
        )
    }

}
