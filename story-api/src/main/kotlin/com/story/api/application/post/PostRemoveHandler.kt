package com.story.api.application.post

import com.story.api.application.component.ComponentCheckHandler
import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.post.PostEventProducer
import com.story.core.domain.post.PostKey
import com.story.core.domain.post.PostRemover
import com.story.core.domain.post.PostSpaceKey
import com.story.core.domain.resource.ResourceId

@HandlerAdapter
class PostRemoveHandler(
    private val postRemover: PostRemover,
    private val postEventProducer: PostEventProducer,
    private val componentCheckHandler: ComponentCheckHandler,
) {

    suspend fun removePost(
        postSpaceKey: PostSpaceKey,
        ownerId: String,
        postId: PostKey,
    ) {
        componentCheckHandler.checkExistsComponent(
            workspaceId = postSpaceKey.workspaceId,
            resourceId = ResourceId.POSTS,
            componentId = postSpaceKey.componentId,
        )

        postRemover.removePost(
            postSpaceKey = postSpaceKey,
            ownerId = ownerId,
            postId = postId,
        )

        postEventProducer.publishDeletedEvent(
            postSpaceKey = postSpaceKey,
            postId = postId,
            ownerId = ownerId,
        )
    }

}
