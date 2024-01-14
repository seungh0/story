package com.story.core.domain.post

import com.story.core.domain.event.BaseEvent
import com.story.core.domain.event.EventAction
import com.story.core.domain.event.EventRecord
import com.story.core.domain.resource.ResourceId
import java.time.LocalDateTime

data class PostEvent(
    val workspaceId: String,
    val resourceId: ResourceId,
    val componentId: String,
    val spaceId: String,
    val postId: Long,
    val accountId: String,
    val title: String?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
) : BaseEvent {

    companion object {
        fun created(post: PostResponse) = EventRecord(
            eventAction = EventAction.CREATED,
            eventKey = PostEventKey(spaceId = post.spaceId, postId = post.postId).makeKey(),
            payload = PostEvent(
                workspaceId = post.workspaceId,
                resourceId = ResourceId.POSTS,
                componentId = post.componentId,
                spaceId = post.spaceId,
                postId = post.postId,
                accountId = post.accountId,
                title = post.title,
                createdAt = post.createdAt,
                updatedAt = post.updatedAt,
            ),
        )

        fun modified(post: PostResponse) = EventRecord(
            eventAction = EventAction.UPDATED,
            eventKey = PostEventKey(spaceId = post.spaceId, postId = post.postId).makeKey(),
            payload = PostEvent(
                workspaceId = post.workspaceId,
                resourceId = ResourceId.POSTS,
                componentId = post.componentId,
                spaceId = post.spaceId,
                postId = post.postId,
                accountId = post.accountId,
                title = post.title,
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
            eventAction = EventAction.DELETED,
            eventKey = PostEventKey(spaceId = spaceId, postId = postId).makeKey(),
            payload = PostEvent(
                workspaceId = workspaceId,
                resourceId = ResourceId.POSTS,
                componentId = componentId,
                spaceId = spaceId,
                postId = postId,
                accountId = accountId,
                title = null,
                createdAt = null,
                updatedAt = null,
            ),
        )
    }

}
