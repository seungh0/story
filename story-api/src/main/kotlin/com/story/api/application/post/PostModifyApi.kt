package com.story.api.application.post

import com.story.api.config.apikey.ApiKeyContext
import com.story.api.config.apikey.RequestApiKey
import com.story.core.common.model.dto.ApiResponse
import com.story.core.domain.post.PostKey
import com.story.core.domain.post.PostSpaceKey
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PostModifyApi(
    private val postModifyHandler: PostModifyHandler,
) {

    /**
     * 포스트 정보를 수정한다
     */
    @PatchMapping("/v1/resources/posts/components/{componentId}/spaces/{spaceId}/posts/{postId}")
    suspend fun patchPost(
        @PathVariable componentId: String,
        @PathVariable spaceId: String,
        @PathVariable postId: PostKey,
        @Valid @RequestBody request: PostModifyApiRequest,
        @RequestApiKey authContext: ApiKeyContext,
    ): ApiResponse<Nothing?> {
        postModifyHandler.patchPost(
            postSpaceKey = PostSpaceKey(
                workspaceId = authContext.workspaceId,
                componentId = componentId,
                spaceId = spaceId,
            ),
            postId = postId,
            ownerId = authContext.getRequiredRequestUserId(),
            request = request,
        )
        return ApiResponse.OK
    }

}
