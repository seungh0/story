package com.story.platform.core.domain.post

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.common.model.AuditingTimeResponse

data class PostResponse(
    val serviceType: ServiceType,
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
                serviceType = post.key.serviceType,
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
