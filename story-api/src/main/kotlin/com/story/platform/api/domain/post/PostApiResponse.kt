package com.story.platform.api.domain.post

import com.story.platform.core.common.model.dto.AuditingTimeResponse
import com.story.platform.core.domain.post.PostResponse

data class PostApiResponse(
    val postId: String,
    val title: String,
    val content: String,
    val isOwner: Boolean,
    val writer: PostWriterApiResponse,
) : AuditingTimeResponse() {

    companion object {
        fun of(post: PostResponse, requestAccountId: String?): PostApiResponse {
            val response = PostApiResponse(
                postId = post.postId.toString(),
                title = post.title,
                content = post.content,
                isOwner = post.accountId == requestAccountId,
                writer = PostWriterApiResponse(
                    accountId = post.accountId,
                )
            )
            response.from(post)
            return response
        }
    }

}
