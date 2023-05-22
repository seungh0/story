package com.story.platform.api.domain.post

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.domain.post.PostModifier
import com.story.platform.core.domain.post.PostSpaceKey
import com.story.platform.core.domain.post.PostSpaceType
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PostModifierApi(
    private val postModifier: PostModifier,
) {

    /**
     * 포스트 정보를 수정한다
     */
    @PutMapping("/v1/spaces/{spaceType}/{spaceId}/posts/{postId}")
    suspend fun modify(
        @PathVariable spaceType: PostSpaceType,
        @PathVariable spaceId: String,
        @PathVariable postId: String,
        @Valid @RequestBody request: PostModifyApiRequest,
    ): ApiResponse<String> {
        postModifier.modify(
            postSpaceKey = PostSpaceKey(
                serviceType = ServiceType.TWEETER,
                spaceType = spaceType,
                spaceId = spaceId,
            ),
            postId = postId,
            accountId = request.accountId,
            title = request.title,
            content = request.content,
            extraJson = request.extraJson,
        )
        return ApiResponse.OK
    }

}
