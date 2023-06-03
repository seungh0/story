package com.story.platform.core.domain.post

import org.springframework.stereotype.Service

@Service
class PostModifyHandler(
    private val postModifier: PostModifier,
    private val postEventPublisher: PostEventPublisher,
) {

    suspend fun modify(
        postSpaceKey: PostSpaceKey,
        accountId: String,
        postId: Long,
        title: String,
        content: String,
        extraJson: String? = null,
    ) {
        postModifier.modify(
            postSpaceKey = postSpaceKey,
            accountId = accountId,
            postId = postId,
            title = title,
            content = content,
            extraJson = extraJson,
        )
        postEventPublisher.publishUpdatedEvent(
            postSpaceKey = postSpaceKey,
            accountId = accountId,
            postId = postId,
            title = title,
            content = content,
            extraJson = extraJson,
        )
    }

}
