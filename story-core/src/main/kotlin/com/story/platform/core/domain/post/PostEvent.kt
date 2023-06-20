package com.story.platform.core.domain.post

import com.story.platform.core.common.enums.EventType
import com.story.platform.core.domain.event.EventRecord

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
            eventType = EventType.POST_CREATED,
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
            eventType = EventType.POST_UPDATED,
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
            eventType = EventType.POST_DELETED,
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
