package com.story.platform.api.domain.post

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.domain.post.PostRegister
import com.story.platform.core.domain.post.PostSpaceKey
import com.story.platform.core.domain.post.PostSpaceType
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PostRegisterApi(
    private val postRegister: PostRegister,
) {

    /**
     * 신규 포스트를 등록한다
     */
    @PostMapping("/v1/spaces/{spaceType}/{spaceId}/posts")
    suspend fun register(
        @PathVariable spaceType: PostSpaceType,
        @PathVariable spaceId: String,
        @Valid @RequestBody request: PostRegisterApiRequest,
    ): ApiResponse<String> {
        postRegister.register(
            postSpaceKey = PostSpaceKey(
                serviceType = ServiceType.TWEETER,
                spaceType = spaceType,
                spaceId = spaceId,
            ),
            accountId = request.accountId,
            title = request.title,
            content = request.content,
            extraJson = request.extraJson,
        )
        return ApiResponse.OK
    }

}
