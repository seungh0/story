package com.story.api.application.post

import com.story.api.application.component.ComponentCheckHandler
import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.post.PostEventProducer
import com.story.core.domain.post.PostKey
import com.story.core.domain.post.PostModifier
import com.story.core.domain.post.PostResponse
import com.story.core.domain.post.PostSpaceKey
import com.story.core.domain.post.section.PostSectionContentRequest
import com.story.core.domain.resource.ResourceId

@HandlerAdapter
class PostModifyHandler(
    private val postModifier: PostModifier,
    private val postEventProducer: PostEventProducer,
    private val componentCheckHandler: ComponentCheckHandler,
) {

    suspend fun patchPost(
        postSpaceKey: PostSpaceKey,
        ownerId: String,
        postId: PostKey,
        title: String?,
        sections: List<PostSectionContentRequest>?,
    ) {
        componentCheckHandler.checkExistsComponent(
            workspaceId = postSpaceKey.workspaceId,
            resourceId = ResourceId.POSTS,
            componentId = postSpaceKey.componentId,
        )

        val (post: PostResponse, hasChanged: Boolean) = postModifier.patchPost(
            postSpaceKey = postSpaceKey,
            ownerId = ownerId,
            postId = postId,
            title = title,
            sections = sections,
        )

        if (!hasChanged) {
            return
        }

        postEventProducer.publishModifiedEvent(post = post)
    }

}
