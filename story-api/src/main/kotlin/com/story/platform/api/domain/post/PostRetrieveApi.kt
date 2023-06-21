package com.story.platform.api.domain.post

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.api.domain.component.ComponentHandler
import com.story.platform.core.common.error.BadRequestException
import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.common.model.CursorRequest
import com.story.platform.core.common.model.CursorResult
import com.story.platform.core.domain.component.ResourceId
import com.story.platform.core.domain.post.PostRetriever
import com.story.platform.core.domain.post.PostSpaceKey
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/v1/posts/components/{componentId}")
@RestController
class PostRetrieveApi(
    private val postRetriever: PostRetriever,
    private val componentHandler: ComponentHandler,
) {

    /**
     * 특정 포스트를 조회한다
     */
    @GetMapping("/spaces/{spaceId}/posts/{postId}")
    suspend fun getPost(
        @PathVariable componentId: String,
        @PathVariable spaceId: String,
        @PathVariable postId: String,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<PostApiResponse> {
        componentHandler.validateComponent(
            workspaceId = authContext.workspaceId,
            resourceId = ResourceId.POSTS,
            componentId = componentId,
        )

        val post = postRetriever.getPost(
            postSpaceKey = PostSpaceKey(
                workspaceId = authContext.workspaceId,
                componentId = componentId,
                spaceId = spaceId,
            ),
            postId = postId.toLongOrNull() ?: throw BadRequestException("잘못된 PostId($postId)이 요청되었습니다"),
        )
        return ApiResponse.success(PostApiResponse.of(post))
    }

    /**
     * 포스트 목록을 조회한다
     */
    @GetMapping("/spaces/{spaceId}/posts")
    suspend fun listPosts(
        @PathVariable componentId: String,
        @PathVariable spaceId: String,
        @Valid cursorRequest: CursorRequest,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<CursorResult<PostApiResponse, String>> {
        componentHandler.validateComponent(
            workspaceId = authContext.workspaceId,
            resourceId = ResourceId.POSTS,
            componentId = componentId,
        )

        val posts = postRetriever.listPosts(
            postSpaceKey = PostSpaceKey(
                workspaceId = authContext.workspaceId,
                componentId = componentId,
                spaceId = spaceId,
            ),
            cursorRequest = cursorRequest,
        )

        val result = CursorResult.of(
            data = posts.data.map { post -> PostApiResponse.of(post) },
            cursor = posts.cursor,
        )

        return ApiResponse.success(result)
    }

}
