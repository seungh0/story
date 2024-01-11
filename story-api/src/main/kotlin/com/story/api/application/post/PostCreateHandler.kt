package com.story.api.application.post

import com.story.api.application.component.ComponentCheckHandler
import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.nonce.NonceManager
import com.story.core.domain.post.PostCreator
import com.story.core.domain.post.PostEventProducer
import com.story.core.domain.post.PostSpaceKey
import com.story.core.domain.post.section.PostSectionContentRequest
import com.story.core.domain.resource.ResourceId

@HandlerAdapter
class PostCreateHandler(
    private val postCreator: PostCreator,
    private val componentCheckHandler: ComponentCheckHandler,
    private val postEventProducer: PostEventProducer,
    private val nonceManager: NonceManager,
) {

    suspend fun createPost(
        postSpaceKey: PostSpaceKey,
        accountId: String,
        title: String,
        sections: List<PostSectionContentRequest>,
        nonce: String?,
    ): Long {
        nonce?.let { nonceManager.verify(nonce) }
        componentCheckHandler.checkExistsComponent(
            workspaceId = postSpaceKey.workspaceId,
            resourceId = ResourceId.POSTS,
            componentId = postSpaceKey.componentId,
        )

        val post = postCreator.createPost(
            postSpaceKey = postSpaceKey,
            accountId = accountId,
            title = title,
            sections = sections,
        )
        postEventProducer.publishCreatedEvent(post = post)
        return post.postId
    }

}
