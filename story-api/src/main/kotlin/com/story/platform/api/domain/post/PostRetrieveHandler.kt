package com.story.platform.api.domain.post

import com.story.platform.api.domain.component.ComponentCheckHandler
import com.story.platform.core.common.annotation.HandlerAdapter
import com.story.platform.core.domain.post.PostIdInvalidException
import com.story.platform.core.domain.post.PostRetriever
import com.story.platform.core.domain.post.PostSpaceKey
import com.story.platform.core.domain.resource.ResourceId

@HandlerAdapter
class PostRetrieveHandler(
    private val postRetriever: PostRetriever,
    private val componentCheckHandler: ComponentCheckHandler,
) {

    suspend fun getPost(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        postId: String,
        requestAccountId: String?,
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
            postId = postId.toLongOrNull() ?: throw PostIdInvalidException("잘못된 PostId($postId)이 요청되었습니다"),
        )
        return PostApiResponse.of(post = post, requestAccountId = requestAccountId)
    }

    suspend fun listPosts(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        request: PostListApiRequest,
        requestAccountId: String?,
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
            cursorRequest = request.toCursor(),
            sortBy = request.sortBy,
        )

        return PostListApiResponse.of(posts = posts, requestAccountId = requestAccountId)
    }

}
