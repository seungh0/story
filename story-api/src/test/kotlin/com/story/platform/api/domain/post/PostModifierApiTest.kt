package com.story.platform.api.domain.post

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.config.AccountIdResolver
import com.story.platform.api.lib.WebClientUtils
import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.domain.post.PostModifier
import com.story.platform.core.domain.post.PostSpaceKey
import com.story.platform.core.domain.post.PostSpaceType
import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(PostModifierApi::class, AccountIdResolver::class)
internal class PostModifierApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val postModifier: PostModifier,
) : FunSpec({

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
            postModifier.modify(
                postSpaceKey = PostSpaceKey(
                    serviceType = ServiceType.TWEETER,
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
        val exchange = webTestClient.put()
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
