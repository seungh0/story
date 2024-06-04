package com.story.api.application.post

import com.story.api.application.component.ComponentCheckHandler
import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.nonce.NonceManager
import com.story.core.domain.post.PostCreator
import com.story.core.domain.post.PostEventProducer
import com.story.core.domain.post.PostId
import com.story.core.domain.post.PostMetadataType
import com.story.core.domain.post.PostModifier
import com.story.core.domain.post.PostNotExistsException
import com.story.core.domain.post.PostParentNotExistsException
import com.story.core.domain.post.PostReader
import com.story.core.domain.post.PostSpaceKey
import com.story.core.domain.post.PostWithSections
import com.story.core.domain.resource.ResourceId

@HandlerAdapter
class PostCreateHandler(
    private val postCreator: PostCreator,
    private val componentCheckHandler: ComponentCheckHandler,
    private val postEventProducer: PostEventProducer,
    private val nonceManager: NonceManager,
    private val postReader: PostReader,
    private val postModifier: PostModifier,
) {

    suspend fun createPost(
        postSpaceKey: PostSpaceKey,
        ownerId: String,
        request: PostCreateRequest,
        nonce: String?,
    ): PostId {
        nonce?.let { nonceManager.verify(nonce) }

        componentCheckHandler.checkExistsComponent(
            workspaceId = postSpaceKey.workspaceId,
            resourceId = ResourceId.POSTS,
            componentId = postSpaceKey.componentId,
        )

        validateParentPost(request, postSpaceKey)

        val post = postCreator.createPost(
            postSpaceKey = postSpaceKey,
            parentId = request.parentId,
            ownerId = ownerId,
            title = request.title,
            sections = request.toSections(),
            extra = request.extra,
        )
        postEventProducer.publishCreatedEvent(post = post)
        return post.postId
    }

    private suspend fun validateParentPost(
        request: PostCreateRequest,
        postSpaceKey: PostSpaceKey,
    ) {
        if (request.parentId == null) {
            return
        }

        try {
            val parentPost = postReader.getPost(
                postSpaceKey = postSpaceKey,
                postId = request.parentId,
            )
            updateParentPostHasChildrenMetadata(parentPost, postSpaceKey)
        } catch (exception: PostNotExistsException) {
            throw PostParentNotExistsException(exception.message, exception)
        }
    }

    private suspend fun updateParentPostHasChildrenMetadata(
        parentPost: PostWithSections,
        postSpaceKey: PostSpaceKey,
    ) {
        if (!parentPost.hasChildrenMetadata()) {
            val hasChanged = postModifier.putMetadata(
                postSpaceKey = postSpaceKey,
                postId = parentPost.postId,
                metadataType = PostMetadataType.HAS_CHILDREN,
                value = true
            )
            if (hasChanged) {
                postEventProducer.publishModifiedEvent(post = parentPost)
            }
        }
    }

}
