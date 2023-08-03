package com.story.platform.api.domain.post

import com.story.platform.api.domain.component.ComponentCheckHandler
import com.story.platform.core.common.model.CursorResult
import com.story.platform.core.common.model.dto.CursorRequest
import com.story.platform.core.domain.post.PostIdInvalidException
import com.story.platform.core.domain.post.PostRetriever
import com.story.platform.core.domain.post.PostSpaceKey
import com.story.platform.core.domain.resource.ResourceId
import org.springframework.stereotype.Service

@Service
class PostRetrieveHandler(
    private val postRetriever: PostRetriever,
    private val componentCheckHandler: ComponentCheckHandler,
) {

    suspend fun getPost(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        postId: String,
    ): PostApiResponse {
        componentCheckHandler.validateComponent(
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
        return PostApiResponse.of(post)
    }

    suspend fun listPosts(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        cursorRequest: CursorRequest,
    ): CursorResult<PostApiResponse, String> {
        componentCheckHandler.validateComponent(
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
            cursorRequest = cursorRequest,
        )

        return CursorResult.of(
            data = posts.data.map { post -> PostApiResponse.of(post) },
            cursor = posts.cursor,
        )
    }

}