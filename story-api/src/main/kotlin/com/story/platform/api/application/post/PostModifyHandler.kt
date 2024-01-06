package com.story.platform.api.application.post

import com.story.platform.api.application.component.ComponentCheckHandler
import com.story.platform.core.common.annotation.HandlerAdapter
import com.story.platform.core.domain.post.PostEventProducer
import com.story.platform.core.domain.post.PostModifier
import com.story.platform.core.domain.post.PostResponse
import com.story.platform.core.domain.post.PostSpaceKey
import com.story.platform.core.domain.post.section.PostSectionContentRequest
import com.story.platform.core.domain.resource.ResourceId

@HandlerAdapter
class PostModifyHandler(
    private val postModifier: PostModifier,
    private val postEventProducer: PostEventProducer,
    private val componentCheckHandler: ComponentCheckHandler,
) {

    suspend fun patchPost(
        postSpaceKey: PostSpaceKey,
        accountId: String,
        postId: Long,
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
            accountId = accountId,
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
