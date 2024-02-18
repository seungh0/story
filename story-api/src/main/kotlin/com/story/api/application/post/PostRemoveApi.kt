package com.story.api.application.post

import com.story.api.config.apikey.ApiKeyContext
import com.story.api.config.apikey.RequestApiKey
import com.story.core.common.model.dto.ApiResponse
import com.story.core.domain.post.PostKey
import com.story.core.domain.post.PostSpaceKey
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class PostRemoveApi(
    private val postRemoveHandler: PostRemoveHandler,
) {

    /**
     * 포스트를 삭제한다
     */
    @DeleteMapping("/v1/resources/posts/components/{componentId}/spaces/{spaceId}/posts/{postId}")
    suspend fun removePost(
        @PathVariable componentId: String,
        @PathVariable spaceId: String,
        @PathVariable postId: PostKey,
        @RequestApiKey authContext: ApiKeyContext,
    ): ApiResponse<Nothing?> {
        postRemoveHandler.removePost(
            postSpaceKey = PostSpaceKey(
                workspaceId = authContext.workspaceId,
                componentId = componentId,
                spaceId = spaceId,
            ),
            ownerId = authContext.getRequiredRequestUserId(),
            postId = postId,
        )

        return ApiResponse.OK
    }

}
