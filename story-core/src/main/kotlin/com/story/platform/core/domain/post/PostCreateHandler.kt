package com.story.platform.core.domain.post

import org.springframework.stereotype.Service

@Service
class PostCreateHandler(
    private val postCreator: PostCreator,
    private val postEventPublisher: PostEventPublisher,
) {

    suspend fun createPost(
        postSpaceKey: PostSpaceKey,
        accountId: String,
        title: String,
        content: String,
        extraJson: String? = null,
    ): Long {
        val post = postCreator.create(
            postSpaceKey = postSpaceKey,
            accountId = accountId,
            title = title,
            content = content,
            extraJson = extraJson,
        )
        postEventPublisher.publishCreatedEvent(post = post)
        return post.postId
    }

}
