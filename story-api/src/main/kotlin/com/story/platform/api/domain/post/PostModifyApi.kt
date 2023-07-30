package com.story.platform.api.domain.post

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.core.common.model.dto.ApiResponse
import com.story.platform.core.domain.post.PostIdInvalidException
import com.story.platform.core.domain.post.PostSpaceKey
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/v1/posts/components/{componentId}")
@RestController
class PostModifyApi(
    private val postModifyHandler: PostModifyHandler,
) {

    /**
     * 포스트 정보를 수정한다
     */
    @PatchMapping("/spaces/{spaceId}/posts/{postId}")
    suspend fun patchPost(
        @PathVariable componentId: String,
        @PathVariable spaceId: String,
        @PathVariable postId: String,
        @Valid @RequestBody request: PostModifyApiRequest,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<Nothing?> {
        postModifyHandler.patchPost(
            postSpaceKey = PostSpaceKey(
                workspaceId = authContext.workspaceId,
                componentId = componentId,
                spaceId = spaceId,
            ),
            postId = postId.toLongOrNull() ?: throw PostIdInvalidException("잘못된 PostId($postId)가 요청되었습니다"),
            accountId = request.accountId,
            title = request.title,
            content = request.content,
            extraJson = request.extraJson,
        )
        return ApiResponse.OK
    }

}
