package com.story.platform.api.domain.post

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.ApiTest
import com.story.platform.api.domain.authentication.AuthenticationHandler
import com.story.platform.api.domain.component.ComponentHandler
import com.story.platform.api.lib.WebClientUtils
import com.story.platform.api.lib.isTrue
import com.story.platform.core.domain.authentication.AuthenticationKeyStatus
import com.story.platform.core.domain.authentication.AuthenticationResponse
import com.story.platform.core.domain.post.PostPatchHandler
import com.story.platform.core.domain.post.PostSpaceKey
import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@ApiTest(PostPatchApi::class)
class PostModifierApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val postPatchHandler: PostPatchHandler,

    @MockkBean
    private val authenticationHandler: AuthenticationHandler,

    @MockkBean
    private val componentHandler: ComponentHandler,
) : FunSpec({

    beforeEach {
        coEvery { authenticationHandler.handleAuthentication(any()) } returns AuthenticationResponse(
            workspaceId = "twitter",
            authenticationKey = "api-key",
            status = AuthenticationKeyStatus.ENABLED,
            description = "",
        )

        coEvery { componentHandler.validateComponent(any(), any(), any()) } returns Unit
    }

    test("기존 포스트를 수정합니다") {
        // given
        val componentId = "post"
        val postId = 100000L
        val spaceId = "계정의 ID"

        val request = PostPatchApiRequest(
            accountId = spaceId,
            title = "토끼가 너무 좋아요",
            content = """
                    끼야아아~
                    내가 만든 쿠키~
                    너를 위해 구워찌이
            """.trimIndent()
        )

        coEvery {
            postPatchHandler.patchPost(
                postSpaceKey = PostSpaceKey(
                    workspaceId = "twitter",
                    componentId = componentId,
                    spaceId = spaceId,
                ),
                postId = postId,
                accountId = spaceId,
                title = request.title,
                content = request.content,
                extraJson = request.extraJson,
            )
        } returns Unit

        // when
        val exchange = webTestClient.patch()
            .uri("/v1/posts/components/{componentId}/spaces/{spaceId}/posts/{postId}", componentId, spaceId, postId)
            .headers(WebClientUtils.commonHeaders)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .jsonPath("$.ok").isTrue()
    }

})
