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

    @GetMapping("/v1/space/{spaceType}/{spaceId}/post/{postId}")
    suspend fun getPost(
        @PathVariable spaceType: PostSpaceType,
        @PathVariable spaceId: String,
        @PathVariable postId: Long,
    ): ApiResponse<PostResponse> {
        val post = postRetriever.findPost(
            postSpaceKey = PostSpaceKey(
                serviceType = ServiceType.TWEETER,
                spaceType = spaceType,
                spaceId = spaceId,
            ),
            postId = postId,
        )
        return ApiResponse.success(PostResponse.of(post))
    }

    @GetMapping("/v1/space/{spaceType}/{spaceId}/posts")
    suspend fun getPosts(
        @PathVariable spaceType: PostSpaceType,
        @PathVariable spaceId: String,
        @Valid cursorRequest: CursorRequest,
    ): ApiResponse<CursorResult<PostResponse, Long>> {
        val posts = postRetriever.findPosts(
            postSpaceKey = PostSpaceKey(
                serviceType = ServiceType.TWEETER,
                spaceType = spaceType,
                spaceId = spaceId,
            ),
            cursorRequest = cursorRequest,
        )

        val result = CursorResult.of(
            data = posts.data.map { post -> PostResponse.of(post) },
            cursor = posts.cursor,
        )

        return ApiResponse.success(result)
    }

}
