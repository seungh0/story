package com.story.api.application.post

import com.story.api.config.apikey.ApiKeyContext
import com.story.api.config.apikey.RequestApiKey
import com.story.core.common.model.dto.ApiResponse
import com.story.core.domain.post.PostKey
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
        @PathVariable postId: PostKey,
        @RequestApiKey authContext: ApiKeyContext,
    ): ApiResponse<PostApiResponse> {
        val response = postRetrieveHandler.getPost(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            postId = postId,
            requestUserId = authContext.requestUserId,
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
        @RequestApiKey authContext: ApiKeyContext,
        @Valid request: PostListApiRequest,
    ): ApiResponse<PostListApiResponse> {
        val response = postRetrieveHandler.listPosts(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            request = request,
            requestUserId = authContext.requestUserId,
        )
        return ApiResponse.ok(response)
    }

}
