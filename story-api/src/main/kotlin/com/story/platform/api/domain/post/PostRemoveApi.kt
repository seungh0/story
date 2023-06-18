package com.story.platform.api.domain.post

import com.story.platform.api.domain.authentication.AuthenticationHandler
import com.story.platform.core.common.error.BadRequestException
import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.domain.post.PostRemoveHandler
import com.story.platform.core.domain.post.PostSpaceKey
import com.story.platform.core.domain.post.PostSpaceType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange

@RestController
class PostRemoveApi(
    private val postRemoveHandler: PostRemoveHandler,
    private val authenticationHandler: AuthenticationHandler,
) {

    /**
     * 포스트를 삭제한다
     */
    @DeleteMapping("/v1/spaces/{spaceType}/{spaceId}/posts/{postId}")
    suspend fun remove(
        @PathVariable spaceType: PostSpaceType,
        @PathVariable spaceId: String,
        @PathVariable postId: String,
        @RequestParam accountId: String,
        serverWebExchange: ServerWebExchange,
    ): ApiResponse<String> {
        val authentication = authenticationHandler.handleAuthentication(serverWebExchange = serverWebExchange)
        postRemoveHandler.remove(
            postSpaceKey = PostSpaceKey(
                serviceType = authentication.serviceType,
                spaceType = spaceType,
                spaceId = spaceId,
            ),
            accountId = accountId,
            postId = postId.toLongOrNull() ?: throw BadRequestException("잘못된 PostId($postId)가 요청되었습니다"),
        )

        return ApiResponse.OK
    }

}
