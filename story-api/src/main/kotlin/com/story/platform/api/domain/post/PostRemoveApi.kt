package com.story.platform.api.domain.post

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.domain.post.PostRemover
import com.story.platform.core.domain.post.PostSpaceKey
import com.story.platform.core.domain.post.PostSpaceType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class PostRemoveApi(
    private val postRemover: PostRemover,
) {

    /**
     * 포스트를 삭제한다
     */
    @DeleteMapping("/v1/space/{spaceType}/{spaceId}/post/{postId}")
    suspend fun remove(
        @PathVariable spaceType: PostSpaceType,
        @PathVariable spaceId: String,
        @PathVariable postId: Long,
        @RequestParam accountId: String,
    ): ApiResponse<String> {
        postRemover.remove(
            postSpaceKey = PostSpaceKey(
                serviceType = ServiceType.TWEETER,
                spaceType = spaceType,
                spaceId = spaceId,
            ),
            accountId = accountId,
            postId = postId,
        )

        return ApiResponse.OK
    }

}
