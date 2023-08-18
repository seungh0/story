package com.story.platform.api.domain.post

import com.story.platform.api.domain.component.ComponentCheckHandler
import com.story.platform.core.common.spring.HandlerAdapter
import com.story.platform.core.domain.post.PostCreator
import com.story.platform.core.domain.post.PostEventProducer
import com.story.platform.core.domain.post.PostSpaceKey
import com.story.platform.core.domain.resource.ResourceId

@HandlerAdapter
class PostCreateHandler(
    private val postCreator: PostCreator,
    private val componentCheckHandler: ComponentCheckHandler,
    private val postEventProducer: PostEventProducer,
) {

    suspend fun createPost(
        postSpaceKey: PostSpaceKey,
        accountId: String,
        title: String,
        content: String,
        extra: Map<String, String>,
    ): Long {
        componentCheckHandler.checkExistsComponent(
            workspaceId = postSpaceKey.workspaceId,
            resourceId = ResourceId.POSTS,
            componentId = postSpaceKey.componentId,
        )

        val post = postCreator.create(
            postSpaceKey = postSpaceKey,
            accountId = accountId,
            title = title,
            content = content,
            extra = extra,
        )
        postEventProducer.publishCreatedEvent(post = post)
        return post.postId
    }

}
