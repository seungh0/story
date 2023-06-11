package com.story.platform.api.domain.post

import com.story.platform.core.common.enums.ServiceType
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

@RestController
class PostModifierApi(
    private val postModifyHandler: PostModifyHandler,
) {

    /**
     * 포스트 정보를 수정한다
     */
    @PatchMapping("/v1/spaces/{spaceType}/{spaceId}/posts/{postId}")
    suspend fun modify(
        @PathVariable spaceType: PostSpaceType,
        @PathVariable spaceId: String,
        @PathVariable postId: String,
        @Valid @RequestBody request: PostModifyPatchApiRequest,
    ): ApiResponse<String> {
        postModifyHandler.patch(
            postSpaceKey = PostSpaceKey(
                serviceType = ServiceType.TWEETER,
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
