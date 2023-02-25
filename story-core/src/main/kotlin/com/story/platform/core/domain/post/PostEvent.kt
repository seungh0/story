package com.story.platform.core.domain.post

import com.story.platform.core.common.enums.ServiceType

data class PostEvent(
    val eventType: PostEventType,
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
        ) = PostEvent(
            eventType = PostEventType.CREATED,
            serviceType = serviceType,
            spaceType = spaceType,
            spaceId = spaceId,
            postId = postId,
            accountId = accountId,
            title = title,
            content = content,
            extraJson = extraJson,
        )

        fun updated(
            serviceType: ServiceType,
            spaceType: PostSpaceType,
            spaceId: String,
            postId: Long,
            accountId: String,
            title: String,
            content: String,
            extraJson: String?,
        ) = PostEvent(
            eventType = PostEventType.UPDATED,
            serviceType = serviceType,
            spaceType = spaceType,
            spaceId = spaceId,
            postId = postId,
            accountId = accountId,
            title = title,
            content = content,
            extraJson = extraJson,
        )

        fun deleted(
            serviceType: ServiceType,
            spaceType: PostSpaceType,
            spaceId: String,
            postId: Long,
            accountId: String,
            title: String,
            content: String,
            extraJson: String?,
        ) = PostEvent(
            eventType = PostEventType.DELETED,
            serviceType = serviceType,
            spaceType = spaceType,
            spaceId = spaceId,
            postId = postId,
            accountId = accountId,
            title = title,
            content = content,
            extraJson = extraJson,
        )
    }

}

enum class PostEventType {

    CREATED,
    UPDATED,
    DELETED,

}
