package com.story.platform.api.domain.post

import com.story.platform.api.domain.component.ComponentCheckHandler
import com.story.platform.core.domain.post.PostEventPublisher
import com.story.platform.core.domain.post.PostModifier
import com.story.platform.core.domain.post.PostResponse
import com.story.platform.core.domain.post.PostSpaceKey
import com.story.platform.core.domain.resource.ResourceId
import org.springframework.stereotype.Service

@Service
class PostModifyHandler(
    private val postModifier: PostModifier,
    private val postEventPublisher: PostEventPublisher,
    private val componentCheckHandler: ComponentCheckHandler,
) {

    suspend fun patchPost(
        postSpaceKey: PostSpaceKey,
        accountId: String,
        postId: Long,
        title: String?,
        content: String?,
        extraJson: String?,
    ) {
        componentCheckHandler.validateComponent(
            workspaceId = postSpaceKey.workspaceId,
            resourceId = ResourceId.POSTS,
            componentId = postSpaceKey.componentId,
        )

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
