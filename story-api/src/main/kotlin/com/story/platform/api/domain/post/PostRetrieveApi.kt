package com.story.platform.api.domain.post

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.core.common.model.dto.ApiResponse
import com.story.platform.core.common.model.dto.CursorRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class PostRetrieveApi(
    private val postRetrieveHandler: PostRetrieveHandler,
) {

    /**
     * 특정 포스트를 조회한다
     */
    @GetMapping("/v1/resources/posts/components/{componentId}/spaces/{spaceId}/posts/{postId}")
    suspend fun getPost(
        @PathVariable componentId: String,
        @PathVariable spaceId: String,
        @PathVariable postId: String,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<PostApiResponse> {
        val response = postRetrieveHandler.getPost(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            postId = postId,
            requestAccountId = authContext.requestAccountId,
        )
        return ApiResponse.ok(response)
    }

    /**
     * 포스트 목록을 조회한다
     */
    @GetMapping("/v1/resources/posts/components/{componentId}/spaces/{spaceId}/posts")
    suspend fun listPosts(
        @PathVariable componentId: String,
        @PathVariable spaceId: String,
        @Valid cursorRequest: CursorRequest,
        @RequestAuthContext authContext: AuthContext,
        @Valid request: PostListApiRequest,
    ): ApiResponse<PostListApiResponse> {
        val response = postRetrieveHandler.listPosts(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            cursorRequest = cursorRequest,
            request = request,
            requestAccountId = authContext.requestAccountId,
        )
        return ApiResponse.ok(response)
    }

}
