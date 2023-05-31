package com.story.platform.api.domain.post

import com.story.platform.core.common.model.AuditingTimeResponse
import com.story.platform.core.domain.post.Post

data class PostApiResponse(
    val postId: String,
    val title: String,
    val content: String,
    val extraJson: String?,
) : AuditingTimeResponse() {

    companion object {
        fun of(post: Post): PostApiResponse {
            val response = PostApiResponse(
                postId = post.key.postId.toString(),
                title = post.title,
                content = post.content,
                extraJson = post.extraJson,
            )
            response.from(post.auditingTime)
            return response
        }
    }

}
