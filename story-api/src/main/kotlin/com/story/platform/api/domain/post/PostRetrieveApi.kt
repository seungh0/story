package com.story.platform.api.domain.post

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.common.model.CursorRequest
import com.story.platform.core.common.model.CursorResult
import com.story.platform.core.domain.post.PostRetriever
import com.story.platform.core.domain.post.PostSpaceKey
import com.story.platform.core.domain.post.PostSpaceType
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class PostRetrieveApi(
    private val postRetriever: PostRetriever,
) {

    /**
     * 특정 포스트를 조회한다
     */
    @GetMapping("/v1/spaces/{spaceType}/{spaceId}/posts/{postId}")
    suspend fun getPost(
        @PathVariable spaceType: PostSpaceType,
        @PathVariable spaceId: String,
        @PathVariable postId: String,
    ): ApiResponse<PostApiResponse> {
        val post = postRetriever.findPost(
            postSpaceKey = PostSpaceKey(
                serviceType = ServiceType.TWEETER,
                spaceType = spaceType,
                spaceId = spaceId,
            ),
            postId = postId,
        )
        return ApiResponse.success(PostApiResponse.of(post))
    }

    /**
     * 포스트 목록을 조회한다
     */
    @GetMapping("/v1/spaces/{spaceType}/{spaceId}/posts")
    suspend fun listPosts(
        @PathVariable spaceType: PostSpaceType,
        @PathVariable spaceId: String,
        @Valid cursorRequest: CursorRequest,
    ): ApiResponse<CursorResult<PostApiResponse, String>> {
        val posts = postRetriever.findPosts(
            postSpaceKey = PostSpaceKey(
                serviceType = ServiceType.TWEETER,
                spaceType = spaceType,
                spaceId = spaceId,
            ),
            cursorRequest = cursorRequest,
        )

        val result = CursorResult.of(
            data = posts.data.map { post -> PostApiResponse.of(post) },
            cursor = posts.cursor,
        )

        return ApiResponse.success(result)
    }

}
