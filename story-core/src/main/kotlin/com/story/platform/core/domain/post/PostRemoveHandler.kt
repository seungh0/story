package com.story.platform.core.domain.post

import org.springframework.stereotype.Service

@Service
class PostRemoveHandler(
    private val postRemover: PostRemover,
    private val postEventPublisher: PostEventPublisher,
) {

    suspend fun remove(
        postSpaceKey: PostSpaceKey,
        accountId: String,
        postId: Long,
    ) {
        postRemover.remove(
            postSpaceKey = postSpaceKey,
            accountId = accountId,
            postId = postId,
        )
        postEventPublisher.publishDeletedEvent(
            postSpaceKey = postSpaceKey,
            postId = postId,
            accountId = accountId,
        )
    }

}
