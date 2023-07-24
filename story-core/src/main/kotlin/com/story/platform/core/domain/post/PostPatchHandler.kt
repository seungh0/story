package com.story.platform.core.domain.post

import org.springframework.stereotype.Service

@Service
class PostPatchHandler(
    private val postModifier: PostModifier,
    private val postEventPublisher: PostEventPublisher,
) {

    suspend fun patchPost(
        postSpaceKey: PostSpaceKey,
        accountId: String,
        postId: Long,
        title: String?,
        content: String?,
        extraJson: String?,
    ) {
        val (post: PostResponse, hasChanged: Boolean) = postModifier.patch(
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

        postEventPublisher.publishModifiedEvent(post = post)
    }

}
