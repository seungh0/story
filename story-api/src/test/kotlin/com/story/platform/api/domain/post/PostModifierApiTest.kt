package com.story.platform.api.domain.post

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.config.auth.AuthContextMethodArgumentResolver
import com.story.platform.api.domain.authentication.AuthenticationHandler
import com.story.platform.api.lib.WebClientUtils
import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.domain.authentication.AuthenticationKeyStatus
import com.story.platform.core.domain.authentication.AuthenticationResponse
import com.story.platform.core.domain.post.PostModifyHandler
import com.story.platform.core.domain.post.PostSpaceKey
import com.story.platform.core.domain.post.PostSpaceType
import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(
    PostModifierApi::class,
    AuthContextMethodArgumentResolver::class
)
internal class PostModifierApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val postModifyHandler: PostModifyHandler,

    @MockkBean
    private val authenticationHandler: AuthenticationHandler,
) : FunSpec({

    beforeEach {
        coEvery { authenticationHandler.handleAuthentication(any()) } returns AuthenticationResponse(
            workspaceId = "twitter",
            apiKey = "api-key",
            status = AuthenticationKeyStatus.ENABLED,
        )
    }

    test("기존 포스트를 수정합니다") {
        // given
        val postId = 100000L
        val spaceId = "계정의 ID"
        val spaceType = PostSpaceType.ACCOUNT

        val request = PostModifyApiRequest(
            accountId = spaceId,
            title = "토끼가 너무 좋아요",
            content = """
                    끼야아아~
                    내가 만든 쿠키~
                    너를 위해 구워찌이
            """.trimIndent()
        )

        coEvery {
            postModifyHandler.patch(
                postSpaceKey = PostSpaceKey(
                    workspaceId = "twitter",
                    spaceId = spaceId,
                    spaceType = spaceType,
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
            .uri("/v1/spaces/{spaceType}/{spaceId}/posts/{postId}", spaceType, spaceId, postId)
            .headers(WebClientUtils.commonHeaders)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .jsonPath("$.result").isEqualTo(ApiResponse.OK.result!!)
    }

})
