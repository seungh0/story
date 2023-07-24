package com.story.platform.core.domain.post

import com.story.platform.core.domain.event.BaseEvent
import com.story.platform.core.domain.event.EventAction
import com.story.platform.core.domain.event.EventKeyGenerator
import com.story.platform.core.domain.event.EventRecord
import com.story.platform.core.domain.resource.ResourceId
import java.time.LocalDateTime

data class PostEvent(
    val spaceId: String,
    val postId: Long,
    val accountId: String,
    val title: String,
    val content: String,
    val extraJson: String?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
) : BaseEvent {

    companion object {
        fun created(post: PostResponse) = EventRecord(
            resourceId = ResourceId.POSTS,
            eventAction = EventAction.CREATED,
            eventKey = EventKeyGenerator.post(spaceId = post.spaceId, postId = post.postId),
            workspaceId = post.workspaceId,
            componentId = post.componentId,
            payload = PostEvent(
                spaceId = post.spaceId,
                postId = post.postId,
                accountId = post.accountId,
                title = post.title,
                content = post.content,
                extraJson = post.extraJson,
                createdAt = post.createdAt,
                updatedAt = post.updatedAt,
            ),
        )

        fun modified(post: PostResponse) = EventRecord(
            resourceId = ResourceId.POSTS,
            eventAction = EventAction.UPDATED,
            eventKey = EventKeyGenerator.post(spaceId = post.spaceId, postId = post.postId),
            workspaceId = post.workspaceId,
            componentId = post.componentId,
            payload = PostEvent(
                spaceId = post.spaceId,
                postId = post.postId,
                accountId = post.accountId,
                title = post.title,
                content = post.content,
                extraJson = post.extraJson,
                createdAt = post.createdAt,
                updatedAt = post.updatedAt,
            ),
        )

        fun deleted(
            workspaceId: String,
            componentId: String,
            spaceId: String,
            postId: Long,
            accountId: String,
        ) = EventRecord(
            resourceId = ResourceId.POSTS,
            eventAction = EventAction.DELETED,
            workspaceId = workspaceId,
            componentId = componentId,
            eventKey = EventKeyGenerator.post(spaceId = spaceId, postId = postId),
            payload = PostEvent(
                spaceId = spaceId,
                postId = postId,
                accountId = accountId,
                title = "",
                content = "",
                extraJson = null,
                createdAt = null,
                updatedAt = null,
            ),
        )
    }

}
