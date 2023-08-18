package com.story.platform.api.domain.post

import com.story.platform.core.common.model.dto.AuditingTimeResponse
import com.story.platform.core.domain.post.PostResponse

data class PostApiResponse(
    val postId: String,
    val title: String,
    val content: String,
    val extra: Map<String, String>,
) : AuditingTimeResponse() {

    companion object {
        fun of(post: PostResponse): PostApiResponse {
            val response = PostApiResponse(
                postId = post.postId.toString(),
                title = post.title,
                content = post.content,
                extra = post.extra,
            )
            response.from(post)
            return response
        }
    }

}
