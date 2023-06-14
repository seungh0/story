package com.story.platform.core.domain.post

import com.story.platform.core.common.enums.EventType
import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.domain.event.EventRecord

data class PostEvent(
    val serviceType: ServiceType,
    val spaceType: PostSpaceType,
    val spaceId: String,
    val postId: Long,
    val accountId: String,
    val title: String,
    val content: String,
    val extraJson: String?,
) {

    companion object {
        fun created(
            serviceType: ServiceType,
            spaceType: PostSpaceType,
            spaceId: String,
            postId: Long,
            accountId: String,
            title: String,
            content: String,
            extraJson: String?,
        ) = EventRecord(
            eventType = EventType.POST_CREATED,
            payload = PostEvent(
                serviceType = serviceType,
                spaceType = spaceType,
                spaceId = spaceId,
                postId = postId,
                accountId = accountId,
                title = title,
                content = content,
                extraJson = extraJson,
            ),
        )

        fun modified(
            serviceType: ServiceType,
            spaceType: PostSpaceType,
            spaceId: String,
            postId: Long,
            accountId: String,
            title: String,
            content: String,
            extraJson: String?,
        ) = EventRecord(
            eventType = EventType.POST_UPDATED,
            payload = PostEvent(
                serviceType = serviceType,
                spaceType = spaceType,
                spaceId = spaceId,
                postId = postId,
                accountId = accountId,
                title = title,
                content = content,
                extraJson = extraJson,
            ),
        )

        fun deleted(
            serviceType: ServiceType,
            spaceType: PostSpaceType,
            spaceId: String,
            postId: Long,
            accountId: String,
        ) = EventRecord(
            eventType = EventType.POST_DELETED,
            payload = PostEvent(
                serviceType = serviceType,
                spaceType = spaceType,
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
