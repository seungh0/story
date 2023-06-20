package com.story.platform.core.domain.post

import com.story.platform.core.common.model.AuditingTimeResponse

data class PostResponse(
    val workspaceId: String,
    val spaceType: PostSpaceType,
    val spaceId: String,
    val postId: Long,
    val accountId: String,
    val title: String,
    val content: String,
    val extraJson: String?,
) : AuditingTimeResponse() {

    companion object {
        fun of(post: Post): PostResponse {
            val response = PostResponse(
                workspaceId = post.key.workspaceId,
                spaceType = post.key.spaceType,
                spaceId = post.key.spaceId,
                postId = post.key.postId,
                accountId = post.accountId,
                title = post.title,
                content = post.content,
                extraJson = post.extraJson,
            )
            response.from(post.auditingTime)
            return response
        }
    }

}
