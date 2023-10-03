package com.story.platform.api.domain.post

import com.story.platform.api.domain.component.ComponentCheckHandler
import com.story.platform.core.common.annotation.HandlerAdapter
import com.story.platform.core.domain.post.PostEventProducer
import com.story.platform.core.domain.post.PostRemover
import com.story.platform.core.domain.post.PostSpaceKey
import com.story.platform.core.domain.resource.ResourceId

@HandlerAdapter
class PostRemoveHandler(
    private val postRemover: PostRemover,
    private val postEventProducer: PostEventProducer,
    private val componentCheckHandler: ComponentCheckHandler,
) {

    suspend fun removePost(
        postSpaceKey: PostSpaceKey,
        accountId: String,
        postId: Long,
    ) {
        componentCheckHandler.checkExistsComponent(
            workspaceId = postSpaceKey.workspaceId,
            resourceId = ResourceId.POSTS,
            componentId = postSpaceKey.componentId,
        )

        postRemover.removePost(
            postSpaceKey = postSpaceKey,
            accountId = accountId,
            postId = postId,
        )

        postEventProducer.publishDeletedEvent(
            postSpaceKey = postSpaceKey,
            postId = postId,
            accountId = accountId,
        )
    }

}
