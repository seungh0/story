package com.story.api.application.post

import com.story.api.config.apikey.ApiKeyContext
import com.story.api.config.apikey.RequestApiKey
import com.story.api.config.nonce.RequestNonce
import com.story.core.common.model.dto.ApiResponse
import com.story.core.domain.post.PostSpaceKey
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PostCreateApi(
    private val postCreateHandler: PostCreateHandler,
) {

    /**
     * 신규 포스트를 등록한다
     */
    @PostMapping("/v1/resources/posts/components/{componentId}/spaces/{spaceId}/posts")
    suspend fun createPost(
        @PathVariable componentId: String,
        @PathVariable spaceId: String,
        @Valid @RequestBody request: PostCreateRequest,
        @RequestApiKey authContext: ApiKeyContext,
        @RequestNonce(required = false) nonce: String? = null,
    ): ApiResponse<PostCreateResponse> {
        val postId = postCreateHandler.createPost(
            postSpaceKey = PostSpaceKey(
                workspaceId = authContext.workspaceId,
                componentId = componentId,
                spaceId = spaceId,
            ),
            ownerId = authContext.getRequiredRequestUserId(),
            nonce = nonce,
            request = request,
        )
        return ApiResponse.ok(PostCreateResponse.of(postId = postId))
    }

}
