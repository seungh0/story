package com.story.platform.core.domain.post

import org.springframework.stereotype.Service

@Service
class PostModifyHandler(
    private val postModifier: PostModifier,
    private val postEventPublisher: PostEventPublisher,
) {

    suspend fun patch(
        postSpaceKey: PostSpaceKey,
        accountId: String,
        postId: Long,
        title: String?,
        content: String?,
        extraJson: String?,
    ) {
        val (post: Post, hasChanged: Boolean) = postModifier.patch(
            postSpaceKey = postSpaceKey,
            accountId = accountId,
            postId = postId,
            title = title,
            content = content,
            extraJson = extraJson,
        )

        if (!hasChanged) {
            return
        }

        postEventPublisher.publishModifiedEvent(
            postSpaceKey = postSpaceKey,
            accountId = accountId,
            postId = postId,
            title = post.title,
            content = post.content,
            extraJson = post.extraJson,
        )
    }

}
