package com.story.platform.api.domain.post

import com.story.platform.api.domain.authentication.AuthenticationHandler
import com.story.platform.core.common.error.BadRequestException
import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.domain.post.PostModifyHandler
import com.story.platform.core.domain.post.PostSpaceKey
import com.story.platform.core.domain.post.PostSpaceType
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange

@RestController
class PostModifierApi(
    private val postModifyHandler: PostModifyHandler,
    private val authenticationHandler: AuthenticationHandler,
) {

    /**
     * 포스트 정보를 수정한다
     */
    @PatchMapping("/v1/spaces/{spaceType}/{spaceId}/posts/{postId}")
    suspend fun patch(
        @PathVariable spaceType: PostSpaceType,
        @PathVariable spaceId: String,
        @PathVariable postId: String,
        @Valid @RequestBody request: PostModifyApiRequest,
        serverWebExchange: ServerWebExchange,
    ): ApiResponse<String> {
        val authentication = authenticationHandler.handleAuthentication(serverWebExchange = serverWebExchange)
        postModifyHandler.patch(
            postSpaceKey = PostSpaceKey(
                serviceType = authentication.serviceType,
                spaceType = spaceType,
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
