package com.story.platform.core.domain.post

import com.story.platform.core.domain.event.EventAction
import com.story.platform.core.domain.event.EventKeyGenerator
import com.story.platform.core.domain.event.EventRecord
import com.story.platform.core.domain.resource.ResourceId

data class PostEvent(
    val workspaceId: String,
    val componentId: String,
    val spaceId: String,
    val postId: Long,
    val accountId: String,
    val title: String,
    val content: String,
    val extraJson: String?,
) {

    companion object {
        fun created(
            workspaceId: String,
            componentId: String,
            spaceId: String,
            postId: Long,
            accountId: String,
            title: String,
            content: String,
            extraJson: String?,
        ) = EventRecord(
            resourceId = ResourceId.POSTS,
            eventAction = EventAction.CREATED,
            eventKey = EventKeyGenerator.post(spaceId = spaceId, postId = postId),
            payload = PostEvent(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
                postId = postId,
                accountId = accountId,
                title = title,
                content = content,
                extraJson = extraJson,
            ),
        )

        fun modified(
            workspaceId: String,
            componentId: String,
            spaceId: String,
            postId: Long,
            accountId: String,
            title: String,
            content: String,
            extraJson: String?,
        ) = EventRecord(
            resourceId = ResourceId.POSTS,
            eventAction = EventAction.UPDATED,
            eventKey = EventKeyGenerator.post(spaceId = spaceId, postId = postId),
            payload = PostEvent(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
                postId = postId,
                accountId = accountId,
                title = title,
                content = content,
                extraJson = extraJson,
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
            eventKey = EventKeyGenerator.post(spaceId = spaceId, postId = postId),
            payload = PostEvent(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
                postId = postId,
                accountId = accountId,
                title = "",
                content = "",
                extraJson = null,
            ),
        )
    }

}
