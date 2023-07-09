package com.story.platform.api.domain.post

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.api.domain.component.ComponentHandler
import com.story.platform.core.common.model.dto.ApiResponse
import com.story.platform.core.domain.post.PostIdInvalidException
import com.story.platform.core.domain.post.PostRemoveHandler
import com.story.platform.core.domain.post.PostSpaceKey
import com.story.platform.core.domain.resource.ResourceId
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/v1/posts/components/{componentId}")
@RestController
class PostRemoveApi(
    private val postRemoveHandler: PostRemoveHandler,
    private val componentHandler: ComponentHandler,
) {

    /**
     * 포스트를 삭제한다
     */
    @DeleteMapping("/spaces/{spaceId}/posts/{postId}")
    suspend fun remove(
        @PathVariable componentId: String,
        @PathVariable spaceId: String,
        @PathVariable postId: String,
        @RequestParam accountId: String,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<Nothing?> {
        componentHandler.validateComponent(
            workspaceId = authContext.workspaceId,
            resourceId = ResourceId.POSTS,
            componentId = componentId,
        )

        postRemoveHandler.remove(
            postSpaceKey = PostSpaceKey(
                workspaceId = authContext.workspaceId,
                componentId = componentId,
                spaceId = spaceId,
            ),
            accountId = accountId,
            postId = postId.toLongOrNull() ?: throw PostIdInvalidException("잘못된 PostId($postId)가 요청되었습니다"),
        )

        return ApiResponse.OK
    }

}
