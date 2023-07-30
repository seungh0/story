package com.story.platform.api.domain.post

import com.story.platform.api.domain.component.ComponentCheckHandler
import com.story.platform.core.domain.post.PostCreator
import com.story.platform.core.domain.post.PostEventPublisher
import com.story.platform.core.domain.post.PostSpaceKey
import com.story.platform.core.domain.resource.ResourceId
import org.springframework.stereotype.Service

@Service
class PostCreateHandler(
    private val postCreator: PostCreator,
    private val componentCheckHandler: ComponentCheckHandler,
    private val postEventPublisher: PostEventPublisher,
) {

    suspend fun createPost(
        postSpaceKey: PostSpaceKey,
        accountId: String,
        title: String,
        content: String,
        extraJson: String? = null,
    ): Long {
        componentCheckHandler.validateComponent(
            workspaceId = postSpaceKey.workspaceId,
            resourceId = ResourceId.POSTS,
            componentId = postSpaceKey.componentId,
        )

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
