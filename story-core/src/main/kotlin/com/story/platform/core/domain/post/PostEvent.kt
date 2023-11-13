package com.story.platform.core.domain.post

import com.story.platform.core.domain.event.BaseEvent
import com.story.platform.core.domain.event.EventAction
import com.story.platform.core.domain.event.EventKeyGenerator
import com.story.platform.core.domain.event.EventRecord
import com.story.platform.core.domain.resource.ResourceId
import java.time.LocalDateTime

data class PostEvent(
    val workspaceId: String,
    val resourceId: ResourceId,
    val componentId: String,
    val spaceId: String,
    val postId: Long,
    val accountId: String,
    val title: String,
    val content: String,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
) : BaseEvent {

    companion object {
        fun created(post: PostResponse) = EventRecord(
            eventAction = EventAction.CREATED,
            eventKey = EventKeyGenerator.post(spaceId = post.spaceId, postId = post.postId),
            payload = PostEvent(
                workspaceId = post.workspaceId,
                resourceId = ResourceId.POSTS,
                componentId = post.componentId,
                spaceId = post.spaceId,
                postId = post.postId,
                accountId = post.accountId,
                title = post.title,
                content = "", // TODO: 섹션으로 변경
                createdAt = post.createdAt,
                updatedAt = post.updatedAt,
            ),
        )

        fun modified(post: PostResponse) = EventRecord(
            eventAction = EventAction.UPDATED,
            eventKey = EventKeyGenerator.post(spaceId = post.spaceId, postId = post.postId),
            payload = PostEvent(
                workspaceId = post.workspaceId,
                resourceId = ResourceId.POSTS,
                componentId = post.componentId,
                spaceId = post.spaceId,
                postId = post.postId,
                accountId = post.accountId,
                title = post.title,
                content = "", // TODO: 섹션으로 변경
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
            eventKey = EventKeyGenerator.post(spaceId = spaceId, postId = postId),
            payload = PostEvent(
                workspaceId = workspaceId,
                resourceId = ResourceId.POSTS,
                componentId = componentId,
                spaceId = spaceId,
                postId = postId,
                accountId = accountId,
                title = "",
                content = "",
                createdAt = null,
                updatedAt = null,
            ),
        )
    }

}
