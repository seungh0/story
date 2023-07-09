package com.story.platform.api.domain.post

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.api.domain.component.ComponentHandler
import com.story.platform.core.common.model.dto.ApiResponse
import com.story.platform.core.domain.post.PostCreateHandler
import com.story.platform.core.domain.post.PostSpaceKey
import com.story.platform.core.domain.resource.ResourceId
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PostCreateApi(
    private val postCreateHandler: PostCreateHandler,
    private val componentHandler: ComponentHandler,
) {

    /**
     * 신규 포스트를 등록한다
     */
    @PostMapping("/v1/posts/components/{componentId}/spaces/{spaceId}/posts")
    suspend fun createPost(
        @PathVariable componentId: String,
        @PathVariable spaceId: String,
        @Valid @RequestBody request: PostCreateApiRequest,
        @RequestAuthContext authContext: AuthContext,
    ): ApiResponse<PostCreateApiResponse> {
        componentHandler.validateComponent(
            workspaceId = authContext.workspaceId,
            resourceId = ResourceId.POSTS,
            componentId = componentId,
        )

        val postId = postCreateHandler.createPost(
            postSpaceKey = PostSpaceKey(
                workspaceId = authContext.workspaceId,
                componentId = componentId,
                spaceId = spaceId,
            ),
            accountId = request.accountId,
            title = request.title,
            content = request.content,
            extraJson = request.extraJson,
        )
        return ApiResponse.ok(PostCreateApiResponse.of(postId = postId))
    }

}
