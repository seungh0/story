package com.story.platform.api.domain.post

import com.story.platform.api.domain.authentication.AuthenticationHandler
import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.domain.post.PostRegisterHandler
import com.story.platform.core.domain.post.PostSpaceKey
import com.story.platform.core.domain.post.PostSpaceType
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange

@RestController
class PostRegisterApi(
    private val postRegisterHandler: PostRegisterHandler,
    private val authenticationHandler: AuthenticationHandler,
) {

    /**
     * 신규 포스트를 등록한다
     */
    @PostMapping("/v1/spaces/{spaceType}/{spaceId}/posts")
    suspend fun register(
        @PathVariable spaceType: PostSpaceType,
        @PathVariable spaceId: String,
        @Valid @RequestBody request: PostRegisterApiRequest,
        serverWebExchange: ServerWebExchange,
    ): ApiResponse<PostRegisterApiResponse> {
        val authentication = authenticationHandler.handleAuthentication(serverWebExchange = serverWebExchange)
        val postId = postRegisterHandler.register(
            postSpaceKey = PostSpaceKey(
                serviceType = authentication.serviceType,
                spaceType = spaceType,
                spaceId = spaceId,
            ),
            accountId = request.accountId,
            title = request.title,
            content = request.content,
            extraJson = request.extraJson,
        )
        return ApiResponse.success(PostRegisterApiResponse.of(postId = postId))
    }

}
