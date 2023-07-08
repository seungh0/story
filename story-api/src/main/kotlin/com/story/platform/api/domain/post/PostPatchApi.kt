package com.story.platform.api.domain.post

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.api.domain.component.ComponentHandler
import com.story.platform.core.common.error.BadRequestException
import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.domain.post.PostPatchHandler
import com.story.platform.core.domain.post.PostSpaceKey
import com.story.platform.core.domain.resource.ResourceId
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/v1/posts/components/{componentId}")
@RestController
class PostPatchApi(
    private val postPatchHandler: PostPatchHandler,
    private val componentHandler: ComponentHandler,
) {

    /**
     * 포스트 정보를 수정한다
     */
    @PatchMapping("/spaces/{spaceId}/posts/{postId}")
    suspend fun patchPost(
        @PathVariable componentId: String,
        @PathVariable spaceId: String,
        @PathVariable postId: String,
        @Valid @RequestBody request: PostPatchApiRequest,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<String> {
        componentHandler.validateComponent(
            workspaceId = authContext.workspaceId,
            resourceId = ResourceId.POSTS,
            componentId = componentId,
        )

        postPatchHandler.patchPost(
            postSpaceKey = PostSpaceKey(
                workspaceId = authContext.workspaceId,
                componentId = componentId,
                spaceId = spaceId,
            ),
            postId = postId.toLongOrNull() ?: throw BadRequestException("잘못된 PostId($postId)가 요청되었습니다"),
            accountId = request.accountId,
            title = request.title,
            content = request.content,
            extraJson = request.extraJson,
        )
        return ApiResponse.OK
    }

}
