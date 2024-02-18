package com.story.api.application.post

import com.story.api.application.component.ComponentCheckHandler
import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.post.PostKey
import com.story.core.domain.post.PostRetriever
import com.story.core.domain.post.PostSpaceKey
import com.story.core.domain.resource.ResourceId

@HandlerAdapter
class PostRetrieveHandler(
    private val postRetriever: PostRetriever,
    private val componentCheckHandler: ComponentCheckHandler,
) {

    suspend fun getPost(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        postId: PostKey,
        requestUserId: String?,
    ): PostApiResponse {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.POSTS,
            componentId = componentId,
        )

        val post = postRetriever.getPost(
            postSpaceKey = PostSpaceKey(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
            ),
            postId = postId,
        )
        return PostApiResponse.of(post = post, requestUserId = requestUserId)
    }

    suspend fun listPosts(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        request: PostListApiRequest,
        requestUserId: String?,
    ): PostListApiResponse {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.POSTS,
            componentId = componentId,
        )

        val posts = postRetriever.listPosts(
            postSpaceKey = PostSpaceKey(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
            ),
            parentId = request.parentId,
            cursorRequest = request.toCursor(),
            sortBy = request.getSortBy(),
        )

        return PostListApiResponse.of(posts = posts, requestUserId = requestUserId)
    }

}
