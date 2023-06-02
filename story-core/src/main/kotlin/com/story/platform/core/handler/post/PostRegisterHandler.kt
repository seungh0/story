package com.story.platform.core.handler.post

import com.story.platform.core.domain.post.PostEventPublisher
import com.story.platform.core.domain.post.PostRegister
import com.story.platform.core.domain.post.PostSpaceKey
import org.springframework.stereotype.Service

@Service
class PostRegisterHandler(
    private val postRegister: PostRegister,
    private val postEventPublisher: PostEventPublisher,
) {

    suspend fun register(
        postSpaceKey: PostSpaceKey,
        accountId: String,
        title: String,
        content: String,
        extraJson: String? = null,
    ): Long {
        val postId = postRegister.register(
            postSpaceKey = postSpaceKey,
            accountId = accountId,
            title = title,
            content = content,
            extraJson = extraJson,
        )
        postEventPublisher.publishCreatedEvent(
            postSpaceKey = postSpaceKey,
            accountId = accountId,
            postId = postId,
            title = title,
            content = content,
            extraJson = extraJson,
        )
        return postId
    }

}
